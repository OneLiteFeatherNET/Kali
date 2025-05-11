plugins {
    java
    `java-library`
}

group = "net.theevilreaper"
version = "0.1.0"


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(platform(libs.mycelium.bom))
    implementation(platform(libs.aonyx.bom))

    compileOnly(libs.aves)
    compileOnly(libs.minestom)

    testImplementation(libs.cyano)
    testImplementation(libs.aves)
    testImplementation(libs.minestom)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testImplementation(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.engine)
}

tasks.test {
    useJUnitPlatform()
}