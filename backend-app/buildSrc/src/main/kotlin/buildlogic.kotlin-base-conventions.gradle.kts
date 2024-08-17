plugins {
    // kotlin gradle plugin: https://kotlinlang.org/docs/gradle.html
    kotlin("jvm")

    // idea gradle plugin: https://docs.gradle.org/current/userguide/idea_plugin.html
    idea
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

group = "dev.woods.booklog"
version = "unspecified"

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(19)
    }
}

tasks.withType<Test> {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

/*
 * Configuring integration tests
 *   - https://docs.gradle.org/8.5/userguide/java_testing.html#sec:configuring_java_integration_tests
 */
val integrationTestSourceSet: SourceSet = sourceSets.create("integrationTest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val integrationTestRuntimeOnly: Configuration by configurations.getting
integrationTestRuntimeOnly.extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    integrationTestImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    integrationTestRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    testClassesDirs = integrationTestSourceSet.output.classesDirs
    classpath = integrationTestSourceSet.runtimeClasspath

    useJUnitPlatform()
}

/*
 * Identifying additional test directories
 *  - https://docs.gradle.org/8.10/userguide/idea_plugin.html#sec:idea_identify_additional_source_sets
 */
idea {
    module {
        testSources.from(integrationTestSourceSet.allSource.srcDirs)
        testResources.from(integrationTestSourceSet.resources.srcDirs)
    }
}
