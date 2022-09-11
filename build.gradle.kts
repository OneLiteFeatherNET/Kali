import org.sonarqube.gradle.SonarQubeTask

plugins {
    java
    alias(libs.plugins.sonarqube)
    jacoco
    alias(libs.plugins.shadow)
}

group = "net.theevilreaper.kali"
val baseVersion = "1.0.0-SNAPSHOT"
val sonarKey = "insert-sonar-key" // TODO: Change me

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(libs.adventureMinimessage)
    implementation(libs.morphia)
    implementation(libs.canis)

    compileOnly(libs.aves)
    compileOnly(libs.minestom)

    testImplementation(libs.aves)
    testImplementation(libs.minestom)
    testImplementation(libs.junitApi)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoJunit)

    testRuntimeOnly(libs.junitEngine)
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

    getByName<SonarQubeTask>("sonarqube") {
        dependsOn(rootProject.tasks.test)
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", sonarKey)
    }
}

if (System.getenv().containsKey("CI")) {
    version = "${baseVersion}+${System.getenv("CI_COMMIT_SHORT_SHA")}"
} else {
    version = baseVersion
}
