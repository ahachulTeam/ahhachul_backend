import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.7.22"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.22"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    kotlin("kapt") version "1.7.22"      // Kotlin Annotation Processor
}

noArg {
    annotation("jakarta.persistence.Entity")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

group = "backend.team"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val asciidoctorExt: Configuration by configurations.creating
val kapt by configurations

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // https://mvnrepository.com/artifact/io.github.microutils/kotlin-logging/3.0.5
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    // https://mvnrepository.com/artifact/org.springframework.restdocs/spring-restdocs-mockmvc/3.0.0
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api/5.9.2
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")

    // https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")

    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl/0.11.5
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // https://mvnrepository.com/artifact/org.springframework/spring-webflux
    implementation("org.springframework:spring-webflux:6.0.7")

    // https://mvnrepository.com/artifact/com.h2database/h2
    runtimeOnly("com.h2database:h2:2.1.214")

    implementation("org.flywaydb:flyway-core:9.17.0")
    implementation("org.flywaydb:flyway-mysql:9.17.0")

    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-aws
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // https://www.testcontainers.org/
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.testcontainers:testcontainers:1.18.1")
    testImplementation("org.testcontainers:junit-jupiter:1.18.1")

    // https://mvnrepository.com/artifact/org.reflections/reflections
    implementation("org.reflections:reflections:0.10.2")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    implementation("org.springframework.boot:spring-boot-starter-quartz:3.0.4")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // https://mvnrepository.com/artifact/io.github.resilience4j/resilience4j-spring-boot3
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.0.2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
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

tasks.register<Copy>("copySecret") {
    from("./ahachul_secret") {
        exclude("application-test.yml")
    }
    into("./src/main/resources")
}

tasks.register<Copy>("copyTestSecret") {
    from("./ahachul_secret") {
        include("application-test.yml")
    }
    into("./src/test/resources")
}

tasks.named("compileJava") {
    dependsOn("copySecret")
    dependsOn("copyTestSecret")
}
