plugins {
	java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.dependency.management)
}

group = "ru.binarysimple"
version = "0.0.2"
description = "Auth Service"

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

extra["springCloudVersion"] = "2025.0.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(libs.liquibase.core)
    implementation(libs.jakarta.validation.api)
    implementation(libs.spring.boot.actuator)
    implementation(libs.postgresql)
    implementation(libs.springdoc.openapi.ui)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.spring.boot.starter.test)
    compileOnly("org.mapstruct:mapstruct:1.6.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.micrometer:micrometer-registry-prometheus")

    implementation(libs.jsonwebtoken.api)
    runtimeOnly(libs.jsonwebtoken.impl)
    runtimeOnly(libs.jsonwebtoken.jackson)
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}

tasks.withType<Test> {
	useJUnitPlatform()
}