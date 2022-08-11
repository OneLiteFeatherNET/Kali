plugins {
    java
    alias(libs.plugins.sonarqube)
}

group = "org.example" // TODO: Change me
val baseVersion = "0.0.1-SNAPSHOT" // TODO: Change me
val sonarKey = "insert-sonar-key" // TODO: Change me

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

    testImplementation(libs.minestom)
    testImplementation(libs.junitApi)
    testRuntimeOnly(libs.junitEngine)
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
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
