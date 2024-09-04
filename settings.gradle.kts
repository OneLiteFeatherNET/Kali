rootProject.name = "Kali"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("minestom", "net.onelitefeather.microtus", "Minestom").version("1.4.2")
            library("minestom-test", "net.onelitefeather.microtus.testing", "testing").version("1.4.2")
            library("adventure.minimessage", "net.kyori", "adventure-text-minimessage").version("4.14.0")
            library("junit.api", "org.junit.jupiter", "junit-jupiter-api").version("5.10.0")
            library("junit.engine", "org.junit.jupiter", "junit-jupiter-engine").version("5.10.0")
            library("mockito.core", "org.mockito", "mockito-core").version("5.6.0")
            library("mockito.junit", "org.mockito", "mockito-junit-jupiter").version("5.6.0")
            library("aves", "de.icevizion.lib", "Aves").version("1.5.2")
            library("canis", "com.github.theEvilReaper", "Canis").version("master-SNAPSHOT")
            library("mini", "net.kyori", "adventure-text-minimessage").version("4.14.0")
            library("morphia", "dev.morphia.morphia", "morphia-core").version("2.2.8")
            plugin("shadow", "com.github.johnrengelman.shadow").version("8.1.1")
            plugin("sonar", "org.sonarqube").version("4.0.0.2929")
        }
    }
}