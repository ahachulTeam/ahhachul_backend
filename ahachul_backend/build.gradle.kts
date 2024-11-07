import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.7.22"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.22"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    kotlin("kapt") version "1.7.22"
}

allprojects {
    group = "backend.team"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.asciidoctor.jvm.convert")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

    noArg {
        annotation("jakarta.persistence.Entity")
    }

    allOpen {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.MappedSuperclass")
        annotation("javax.persistence.Embeddable")
    }

    java.sourceCompatibility = JavaVersion.VERSION_17

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        // TODO: infra-module 분리
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation("org.springframework.boot:spring-boot-starter-web")
        testImplementation("org.springframework.boot:spring-boot-starter-test")

        // https://mvnrepository.com/artifact/io.github.microutils/kotlin-logging/3.0.5
        implementation("io.github.microutils:kotlin-logging:3.0.5")

        // https://mvnrepository.com/artifact/com.h2database/h2
        runtimeOnly("com.h2database:h2:2.1.214")

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api/5.9.2
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")

        // https://www.testcontainers.org/
        testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
        testImplementation("org.testcontainers:testcontainers:1.18.1")
        testImplementation("org.testcontainers:junit-jupiter:1.18.1")

        runtimeOnly("com.mysql:mysql-connector-j")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
    }

    // Kotlin compile 설정
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    // application.yml 파일을 submodule(ahachul_secret) -> resources 경로로 복사
    tasks.register<Copy>("copySecret") {
        from("../ahachul_secret") {
            exclude("application-test.yml")
        }
        into("src/main/resources")
    }

    tasks.register<Copy>("copyTestSecret") {
        from("../ahachul_secret") {
            include("application-test.yml")
        }
        into("src/test/resources")
    }

    tasks.named("compileJava") {
        dependsOn("copySecret")
        dependsOn("copyTestSecret")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks.getByName<Jar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

project(":core-module") {
    val kapt by configurations

    dependencies {
        // flyway
        implementation("org.flywaydb:flyway-core:9.17.0")
        implementation("org.flywaydb:flyway-mysql:9.17.0")

        // QueryDSL
        implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
        kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    }

    // QueryDSL QClass 파일 생성 경로 지정
    sourceSets {
        main {
            java.srcDir("$buildDir/generated/source/kapt/main")
        }
    }

    // BootJar 비활성화
    tasks.getByName<Jar>("bootJar") {
        enabled = false
    }

    tasks.getByName<Jar>("jar") {
        enabled = true
    }
}

/**
 * Application 모듈
 */
project(":application-module") {
    val asciidoctorExt: Configuration by configurations.creating
    dependencies {
        implementation(project(":core-module"))

        // spring boot
        implementation("org.springframework.boot:spring-boot-starter-aop")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        // https://mvnrepository.com/artifact/org.springframework.restdocs/spring-restdocs-mockmvc/3.0.0
        testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

        // https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/
        asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")

        // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl/0.11.5
        implementation("io.jsonwebtoken:jjwt-api:0.11.5")
        implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
        implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

        // https://mvnrepository.com/artifact/org.springframework/spring-webflux
        implementation("org.springframework:spring-webflux:6.0.7")

        // https://mvnrepository.com/artifact/io.github.resilience4j/resilience4j-spring-boot3
        implementation("io.github.resilience4j:resilience4j-spring-boot3:2.0.2")
        implementation("org.springframework.boot:spring-boot-starter-actuator")

        // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-aws
        implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")
    }

    val snippetsDir by extra {
        file("build/generated-snippets")
    }

    tasks {
        asciidoctor {
            dependsOn(test)
            configurations("asciidoctorExt")
            baseDirFollowsSourceFile()
            inputs.dir(snippetsDir)
        }
        register<Copy>("copyDocument") {
            dependsOn(asciidoctor)
            from(file("build/docs/asciidoc"))
            into(file("src/main/resources/static/docs"))
        }
        bootJar {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            dependsOn("copyDocument")
            from(asciidoctor.get().outputDir) {
                into("BOOT-INF/classes/static/docs")
            }
        }
    }

    tasks.getByName<Jar>("bootJar") {
        enabled = true
    }

    tasks.getByName<Jar>("jar") {
        enabled = false
    }
}

/**
 * 스케줄러(배치) 모듈
 */
project(":schedule-module") {
    dependencies {
        implementation(project(":core-module"))

        // https://mvnrepository.com/artifact/org.reflections/reflections
        implementation("org.reflections:reflections:0.10.2")
        implementation("com.google.code.gson:gson:2.9.0")
        implementation("org.springframework.boot:spring-boot-starter-quartz:3.0.4")
    }

    tasks.getByName<Jar>("bootJar") {
        enabled = true
    }

    tasks.getByName<Jar>("jar") {
        enabled = false
    }
}
