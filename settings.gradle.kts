rootProject.name = "kali"

dependencyResolutionManagement {
    pluginManagement {
        repositories {
            gradlePluginPortal()
            maven("https://eldonexus.de/repository/maven-public/")
        }
    }
    repositories {
        if (System.getenv("CI") != null) {
            repositoriesMode = RepositoriesMode.PREFER_SETTINGS
            repositories {
                maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                maven("https://repo.htl-md.schule/repository/Gitlab-Runner/")
                maven {
                    val groupdId = 28 // Gitlab Group
                    val ciApiv4Url = System.getenv("CI_API_V4_URL")
                    url = uri("$ciApiv4Url/groups/$groupdId/-/packages/maven")
                    name = "GitLab"
                    credentials(HttpHeaderCredentials::class.java) {
                        name = "Job-Token"
                        value = System.getenv("CI_JOB_TOKEN")
                    }
                    authentication {
                        create<HttpHeaderAuthentication>("header")
                    }
                }
            }
        } else {
            repositories {
                maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                maven {
                    val groupdId = 28 // Gitlab Group
                    url = uri("https://gitlab.onelitefeather.dev/api/v4/groups/$groupdId/-/packages/maven")
                    name = "GitLab"
                    credentials(HttpHeaderCredentials::class.java) {
                        name = "Private-Token"
                        value = providers.gradleProperty("gitLabPrivateToken").get()
                    }
                    authentication {
                        create<HttpHeaderAuthentication>("header")
                    }
                }
                maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                mavenCentral()
                maven("https://jitpack.io")
            }
        }
    }
    versionCatalogs {
        create("libs") {
            version("publishdata", "1.4.0")
            library("minestom", "net.onelitefeather.microtus", "Microtus").version("1.4.2")
            library("minestom-test", "net.onelitefeather.microtus.testing", "testing").version("1.4.2")
            library("adventure.minimessage", "net.kyori", "adventure-text-minimessage").version("4.14.0")
            library("junit.api", "org.junit.jupiter", "junit-jupiter-api").version("5.10.0")
            library("junit.engine", "org.junit.jupiter", "junit-jupiter-engine").version("5.10.0")
            library("mockito.core", "org.mockito", "mockito-core").version("5.6.0")
            library("mockito.junit", "org.mockito", "mockito-junit-jupiter").version("5.6.0")
            library("aves", "de.icevizion.lib", "aves").version("1.5.2")
            library("canis", "com.github.theEvilReaper", "Canis").version("master-SNAPSHOT")
            library("mini", "net.kyori", "adventure-text-minimessage").version("4.14.0")
            library("morphia", "dev.morphia.morphia", "morphia-core").version("2.2.8")

            plugin("shadow", "com.github.johnrengelman.shadow").version("8.1.1")
            plugin("publishdata", "de.chojo.publishdata").versionRef("publishdata")
        }
    }
}
