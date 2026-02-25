#!/usr/bin/env node

import fs from "fs/promises";
import path from "path";

const ROOT = process.cwd();
const BENCHMARK_DIR = path.resolve(ROOT, "docs/benchmarks");
const API_BASE = process.env.AHHACHUL_API_BASE ?? "http://localhost:8080";
const BASELINE_ARG = process.argv[2] ?? "";

function collapseLineSequenceFromEdges(edges) {
  const sequence = [];
  for (const edge of edges ?? []) {
    const name = edge?.subwayLineName;
    if (!name) continue;
    if (sequence[sequence.length - 1] !== name) {
      sequence.push(name);
    }
  }
  return sequence;
}

function arraysEqual(left, right) {
  if (left.length !== right.length) return false;
  for (let i = 0; i < left.length; i += 1) {
    if (left[i] !== right[i]) return false;
  }
  return true;
}

function safeMean(values) {
  if (!values.length) return null;
  return Number((values.reduce((sum, value) => sum + value, 0) / values.length).toFixed(3));
}

async function loadBaselinePath() {
  if (BASELINE_ARG) {
    return path.resolve(ROOT, BASELINE_ARG);
  }

  const files = await fs.readdir(BENCHMARK_DIR);
  const candidates = files
    .filter((file) => file.startsWith("kakao_od30_baseline_") && file.endsWith(".json"))
    .sort();
  if (!candidates.length) {
    throw new Error("baseline file not found in docs/benchmarks");
  }
  return path.resolve(BENCHMARK_DIR, candidates[candidates.length - 1]);
}

async function getStationNameToId() {
  const res = await fetch(`${API_BASE}/v1/subway-lines`);
  if (!res.ok) {
    throw new Error(`failed to load station list: ${res.status}`);
  }
  const body = await res.json();
  if (Number(body?.code) !== 100) {
    throw new Error(`station list business failure: code=${body?.code}`);
  }

  const map = new Map();
  for (const line of body.result?.subwayLines ?? []) {
    for (const station of line?.stations ?? []) {
      if (!station?.name || !station?.id) continue;
      if (!map.has(station.name)) {
        map.set(station.name, station.id);
      }
    }
  }
  return map;
}

async function fetchAhhachulRoute(sourceStationId, destinationStationId) {
  const params = new URLSearchParams({
    sourceStationId: String(sourceStationId),
    destinationStationId: String(destinationStationId),
    strategy: "BALANCED",
    alternatives: "3",
  });
  const res = await fetch(`${API_BASE}/v2/subway/routes/search?${params.toString()}`);
  if (!res.ok) {
    throw new Error(`http=${res.status}`);
  }

  const body = await res.json();
  if (Number(body?.code) !== 100) {
    throw new Error(`business_code=${body?.code}`);
  }

  const route = body.result?.routes?.[0];
  if (!route) {
    throw new Error("empty route");
  }

  const lines = collapseLineSequenceFromEdges(route.edges);
  const transferCount = Number(route.summary?.transferCount ?? 0);
  const estimatedMinutes = Number(route.summary?.estimatedMinutes ?? 0);

  return {
    lineSequence: lines,
    lineSignature: `${lines.join(">")}|T${transferCount}`,
    transferCount,
    estimatedMinutes,
    totalStops: Number(route.summary?.totalStops ?? 0),
  };
}

async function main() {
  const baselinePath = await loadBaselinePath();
  const baseline = JSON.parse(await fs.readFile(baselinePath, "utf8"));
  const nameToId = await getStationNameToId();

  const rows = [];
  const comparable = [];

  for (const row of baseline.rows ?? []) {
    if (row.error || !row.kakao) {
      rows.push({
        index: row.index,
        sourceName: row.sourceName,
        destinationName: row.destinationName,
        status: "SKIPPED_BASELINE_ERROR",
        error: row.error ?? "kakao data missing",
      });
      continue;
    }

    const sourceStationId = nameToId.get(row.sourceName);
    const destinationStationId = nameToId.get(row.destinationName);
    if (!sourceStationId || !destinationStationId) {
      rows.push({
        index: row.index,
        sourceName: row.sourceName,
        destinationName: row.destinationName,
        status: "SKIPPED_STATION_NOT_FOUND",
      });
      continue;
    }

    try {
      const ahhachul = await fetchAhhachulRoute(sourceStationId, destinationStationId);
      const kakao = row.kakao;
      const routeMatch = arraysEqual(ahhachul.lineSequence, kakao.lineSequence) &&
        ahhachul.transferCount === kakao.transferCount;
      const timeAbsError = Number(Math.abs(ahhachul.estimatedMinutes - kakao.minutes).toFixed(3));

      const resultRow = {
        index: row.index,
        sourceName: row.sourceName,
        destinationName: row.destinationName,
        sourceStationId,
        destinationStationId,
        routeMatch,
        timeAbsError,
        kakao,
        ahhachul,
      };
      rows.push(resultRow);
      comparable.push(resultRow);
      console.log(
        `[OK] ${row.index} ${row.sourceName}->${row.destinationName} | match=${routeMatch ? "Y" : "N"} | MAE=${timeAbsError}`
      );
    } catch (error) {
      rows.push({
        index: row.index,
        sourceName: row.sourceName,
        destinationName: row.destinationName,
        status: "FAIL_AHHACHUL",
        error: String(error),
      });
      console.log(`[FAIL] ${row.index} ${row.sourceName}->${row.destinationName} :: ${String(error)}`);
    }
  }

  const routeMatchCount = comparable.filter((row) => row.routeMatch).length;
  const timeMae = safeMean(comparable.map((row) => row.timeAbsError));
  const transferMae = safeMean(
    comparable.map((row) => Math.abs(row.ahhachul.transferCount - row.kakao.transferCount))
  );

  const payload = {
    generatedAt: new Date().toISOString(),
    baselinePath,
    apiBase: API_BASE,
    totalBaselineRows: baseline.rows?.length ?? 0,
    comparableCount: comparable.length,
    routeMatchCount,
    routeMatchRate: comparable.length ? Number((routeMatchCount / comparable.length).toFixed(4)) : null,
    timeMae,
    transferMae,
    rows,
  };

  await fs.mkdir(BENCHMARK_DIR, { recursive: true });
  const stamp = new Date().toISOString().replace(/[:.]/g, "-");
  const outPath = path.resolve(BENCHMARK_DIR, `ahhachul_vs_kakao_od30_report_${stamp}.json`);
  await fs.writeFile(outPath, `${JSON.stringify(payload, null, 2)}\n`, "utf8");
  console.log(`\nSaved report: ${outPath}`);
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
