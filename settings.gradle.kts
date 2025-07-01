rootProject.name = "kali"

dependencyResolutionManagement {
    pluginManagement {
        repositories {
            gradlePluginPortal()
            maven("https://eldonexus.de/repository/maven-public/")
        }
    }
    repositories {
        mavenCentral()
        maven {
            name = "OneLiteFeatherRepository"
            url = uri("https://repo.onelitefeather.dev/onelitefeather")
            if (System.getenv("CI") != null) {
                credentials {
                    username = System.getenv("ONELITEFEATHER_MAVEN_USERNAME")
                    password = System.getenv("ONELITEFEATHER_MAVEN_PASSWORD")
                }
            } else {
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            version("publishdata", "1.4.0")
            version("shadow", "8.3.8")

            version("bom", "1.2.3")
            version("aonyx", "0.3.1")

            library("mycelium.bom", "net.onelitefeather", "mycelium-bom").versionRef("bom")
            library("aonyx.bom", "net.onelitefeather", "aonyx-bom").versionRef("aonyx")

            library("aves", "net.theevilreaper", "aves").withoutVersion()
            library("xerus", "net.theevilreaper", "xerus").withoutVersion()
            library("guira", "net.onelitefeather", "guira").withoutVersion()

            library("minestom", "net.minestom", "minestom-snapshots").withoutVersion()
            library("cyano", "net.onelitefeather", "cyano").withoutVersion()
            library("adventure.minimessage", "net.kyori", "adventure-text-minimessage").withoutVersion()
            library("junit.api", "org.junit.jupiter", "junit-jupiter-api").withoutVersion()
            library("junit.engine", "org.junit.jupiter", "junit-jupiter-engine").withoutVersion()
            library("junit.params", "org.junit.jupiter", "junit-jupiter-params").withoutVersion()
            library("junit.platform.launcher", "org.junit.platform", "junit-platform-launcher").withoutVersion()

            library("mockito.core", "org.mockito", "mockito-core").withoutVersion()
            library("mockito.junit", "org.mockito", "mockito-junit-jupiter").withoutVersion()

            library("morphia", "dev.morphia.morphia", "morphia-core").version("2.5.0")

            plugin("shadow", "com.gradleup.shadow").versionRef("shadow")
            plugin("publishdata", "de.chojo.publishdata").versionRef("publishdata")
        }
    }
}

include("common")