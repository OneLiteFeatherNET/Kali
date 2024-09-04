plugins {
    java
    jacoco
    alias(libs.plugins.shadow)
    alias(libs.plugins.sonar)
}

group = "net.theevilreaper.kali"
val baseVersion = "1.0.0-SNAPSHOT"
val sonarKey = "dungeon_projects_kali_AYMuSYZaWr3PlWoLMa-G"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven {
        val groupdId = 28 // Gitlab Group
        url = if (System.getenv().containsKey("CI")) {
            val ciApiv4Url = System.getenv("CI_API_V4_URL")
            uri("$ciApiv4Url/groups/$groupdId/-/packages/maven")
        } else {
            uri("https://gitlab.themeinerlp.dev/api/v4/groups/$groupdId/-/packages/maven")
        }
        name = "GitLab"
        credentials(HttpHeaderCredentials::class.java) {
            name = if (System.getenv().containsKey("CI")) {
                "Job-Token"
            } else {
                "Private-Token"
            }
            value = if (System.getenv().containsKey("CI")) {
                System.getenv("CI_JOB_TOKEN")
            } else {
                val gitLabPrivateToken: String? by project
                println(gitLabPrivateToken)
                gitLabPrivateToken
            }
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
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

sonarqube {
    properties {
        property("sonar.projectKey", sonarKey)
        property("sonar.qualitygate.wait", true)
        property("sonar.qualitygate.wait", true)
    }
}

version = if (System.getenv().containsKey("CI")) {
    "${baseVersion}+${System.getenv("CI_COMMIT_SHORT_SHA")}"
} else {
    baseVersion
}
