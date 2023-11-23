plugins {
    java
    jacoco
    alias(libs.plugins.sonar)
}

group = "org.example" // TODO: Change me
val baseVersion = "0.0.1-SNAPSHOT" // TODO: Change me
val sonarKey = "dungeon_zosma_AYRjIidNwVDHzVoeOyqG" // TODO: Change me

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(libs.minestom)
    testImplementation(libs.minestom.test)
    testImplementation(libs.minestom)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
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

sonarqube {
    properties {
        property("sonar.projectKey", "dungeon_zosma_AYm_wAIFq35l90nqW9Qs")
        property("sonar.projectName", "Zosma")
        property("sonar.qualitygate.wait", true)
    }
}

version = if (System.getenv().containsKey("CI")) {
    "${baseVersion}+${System.getenv("CI_COMMIT_SHORT_SHA")}"
} else {
    baseVersion
}
