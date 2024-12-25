plugins {
    java
    jacoco
    alias(libs.plugins.shadow)
    alias(libs.plugins.publishdata)
}

group = "net.theevilreaper.kali"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(platform(libs.microtus.bom))
    implementation(platform(libs.dungeon.bom))
    implementation(libs.mini)
    implementation(libs.morphia)

    compileOnly(libs.aves)
    compileOnly(libs.minestom)

    testImplementation(libs.aves)
    testImplementation(libs.minestom)
    testImplementation(libs.minestom.test)
    testImplementation(libs.junitApi)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoJunit)

    testRuntimeOnly(libs.junitEngine)
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
}

publishData {
    addBuildData()
    useGitlabReposForProject("", "")
    publishTask("jar")
}
