#!/usr/bin/env node

import fs from "fs/promises";
import path from "path";

const ROOT = process.cwd();
const ODS_PATH = path.resolve(ROOT, "scripts/benchmark/od30.json");
const OUT_DIR = path.resolve(ROOT, "docs/benchmarks");

const KAKAO_HEADERS = {
  Referer: "https://map.kakao.com/",
  "User-Agent": "Mozilla/5.0",
};

function toStationQuery(name) {
  return name.endsWith("역") ? name : `${name}역`;
}

function parseJsonp(raw) {
  const text = raw.trim();
  if (!text) {
    throw new Error("empty response");
  }

  if (text.startsWith("{") || text.startsWith("[")) {
    return JSON.parse(text);
  }

  const start = text.indexOf("(");
  const end = text.lastIndexOf(")");
  if (start < 0 || end < 0 || end <= start) {
    throw new Error(`invalid jsonp payload: ${text.slice(0, 80)}`);
  }
  return JSON.parse(text.slice(start + 1, end));
}

async function getJsonp(url) {
  const res = await fetch(url, { headers: KAKAO_HEADERS });
  const text = await res.text();
  if (!res.ok) {
    throw new Error(`request failed: ${res.status} ${url}`);
  }
  return parseJsonp(text);
}

function collapseLineSequence(lines) {
  const collapsed = [];
  for (const line of lines) {
    if (!line) continue;
    if (collapsed[collapsed.length - 1] !== line) {
      collapsed.push(line);
    }
  }
  return collapsed;
}

async function searchKakaoSubwayPoint(query) {
  const params = new URLSearchParams({
    query,
    msFlag: "S",
    gb: "R",
    sort: "0",
    callback: "cb",
  });
  const data = await getJsonp(`https://search.map.kakao.com/mapsearch/map.daum?${params.toString()}`);
  const places = Array.isArray(data.place) ? data.place : [];

  const subwayPlaces = places.filter((place) =>
    place &&
    place.source === "subway" &&
    typeof place.x === "number" &&
    typeof place.y === "number" &&
    typeof place.sourceId === "string" &&
    place.sourceId.length > 0
  );

  const picked = subwayPlaces[0];
  if (!picked) {
    throw new Error(`subway point not found for query=${query}`);
  }

  return {
    name: picked.name,
    x: picked.x,
    y: picked.y,
    sourceId: picked.sourceId,
    confirmId: picked.confirmid ?? null,
  };
}

async function fetchKakaoSubwayRoute(sourcePoint, destinationPoint) {
  const params = new URLSearchParams({
    inputCoordSystem: "WCONGNAMUL",
    outputCoordSystem: "WCONGNAMUL",
    service: "map.daum.net",
    callback: "cb",
    sX: String(sourcePoint.x),
    sY: String(sourcePoint.y),
    sName: sourcePoint.name,
    sid: sourcePoint.sourceId,
    eX: String(destinationPoint.x),
    eY: String(destinationPoint.y),
    eName: destinationPoint.name,
    eid: destinationPoint.sourceId,
  });

  const routeData = await getJsonp(`https://map.kakao.com/route/pubtrans.json?${params.toString()}`);
  const inLocal = routeData.in_local;
  const routes = Array.isArray(inLocal?.routes) ? inLocal.routes : [];
  const subwayRoute = routes.find((route) => route?.type === "SUBWAY");

  if (!subwayRoute) {
    throw new Error(`subway route not found: status=${inLocal?.status ?? "UNKNOWN"}`);
  }

  const lines = collapseLineSequence(
    (subwayRoute.summaries ?? [])
      .map((summary) => summary?.vehicles?.[0]?.name ?? null)
  );

  const seconds = Number(subwayRoute.time?.value ?? 0);
  return {
    ranking: subwayRoute.ranking ?? null,
    minutes: Number((seconds / 60).toFixed(2)),
    transferCount: Number(subwayRoute.transfers ?? 0),
    lineSequence: lines,
    lineSignature: `${lines.join(">")}|T${Number(subwayRoute.transfers ?? 0)}`,
    rawStatus: inLocal?.status ?? null,
  };
}

function timestamp() {
  const now = new Date();
  const y = now.getFullYear();
  const m = String(now.getMonth() + 1).padStart(2, "0");
  const d = String(now.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
}

async function main() {
  const ods = JSON.parse(await fs.readFile(ODS_PATH, "utf8"));
  const rows = [];

  for (let i = 0; i < ods.length; i += 1) {
    const od = ods[i];
    const sourceQuery = toStationQuery(od.sourceName);
    const destinationQuery = toStationQuery(od.destinationName);

    try {
      const sourcePoint = await searchKakaoSubwayPoint(sourceQuery);
      const destinationPoint = await searchKakaoSubwayPoint(destinationQuery);
      const kakao = await fetchKakaoSubwayRoute(sourcePoint, destinationPoint);
      rows.push({
        index: i + 1,
        sourceName: od.sourceName,
        destinationName: od.destinationName,
        sourceQuery,
        destinationQuery,
        sourcePoint,
        destinationPoint,
        kakao,
      });
      console.log(`[OK] ${i + 1}/${ods.length} ${od.sourceName} -> ${od.destinationName} (${kakao.minutes}m, T${kakao.transferCount})`);
    } catch (error) {
      rows.push({
        index: i + 1,
        sourceName: od.sourceName,
        destinationName: od.destinationName,
        sourceQuery,
        destinationQuery,
        error: String(error),
      });
      console.log(`[FAIL] ${i + 1}/${ods.length} ${od.sourceName} -> ${od.destinationName} :: ${String(error)}`);
    }
  }

  const successCount = rows.filter((row) => !row.error).length;
  const payload = {
    generatedAt: new Date().toISOString(),
    source: "KakaoMap route/pubtrans + mapsearch",
    odSetName: "OD30_FIXED_2026-02-25",
    total: rows.length,
    successCount,
    failCount: rows.length - successCount,
    rows,
  };

  await fs.mkdir(OUT_DIR, { recursive: true });
  const outPath = path.resolve(OUT_DIR, `kakao_od30_baseline_${timestamp()}.json`);
  await fs.writeFile(outPath, `${JSON.stringify(payload, null, 2)}\n`, "utf8");
  console.log(`\nSaved baseline: ${outPath}`);
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
