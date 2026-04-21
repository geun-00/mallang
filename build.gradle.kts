plugins {
    java
    id("org.springframework.boot") version "3.5.11"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}

jacoco {
    toolVersion = "0.8.11"
}

val jacocoExcludes = listOf(
    "io/mallang/MallangApplication.class",
    "io/mallang/P6SpyFormatter.class",
    "io/mallang/adapter/config/**",
    "io/mallang/adapter/web/GlobalExceptionHandler.class",
    "**/adapter/web/model/**",
    "**/application/provided/**/model/**",
    "**/application/shared/query/SliceResult.class",
    "**/*JpaRepository.class",
    "**/domain/exception/**",
    "**/domain/common/exception/**"
)

group = "io"
version = "0.0.1-SNAPSHOT"
description = "mallang"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.github.f4b6a3:ulid-creator:5.2.3")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.5")
    implementation("org.flywaydb:flyway-core")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jsoup:jsoup:1.18.3")
    testImplementation("org.apache.httpcomponents.client5:httpclient5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:3.0.5")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    classDirectories.setFrom(
        files(sourceSets.main.get().output.asFileTree.matching {
            exclude(jacocoExcludes)
        })
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)

        // 리포트 출력 경로 설정
        html.outputLocation.set(
            layout.buildDirectory.dir("reports/jacoco/test/html")
        )
        xml.outputLocation.set(
            layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml")
        )
    }
}

// 커버리지 검증
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    classDirectories.setFrom(
        files(sourceSets.main.get().output.asFileTree.matching {
            exclude(jacocoExcludes)
        })
    )

    violationRules {
        rule {
            element = "BUNDLE"

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.60".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
