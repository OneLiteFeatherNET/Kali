plugins {
    java
    jacoco
    alias(libs.plugins.shadow)
}

group = "net.theevilreaper.kali"
val baseVersion = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(libs.mini)
    implementation(libs.morphia)
    implementation(libs.canis)

    compileOnly(libs.aves)
    compileOnly(libs.minestom)
    testImplementation(libs.minestom.test)
    testImplementation(libs.minestom)
    testImplementation(libs.junit.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
    testRuntimeOnly(libs.junit.engine)
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    jacocoTestReport {
        dependsOn(rootProject.tasks.test)
        reports {
            xml.required.set(true)
        }
    }

    test {
        finalizedBy(rootProject.tasks.jacocoTestReport)
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    getByName("sonar") {
        dependsOn(rootProject.tasks.test)
    }
}

version = if (System.getenv().containsKey("CI")) {
    "${baseVersion}+${System.getenv("CI_COMMIT_SHORT_SHA")}"
} else {
    baseVersion
}
