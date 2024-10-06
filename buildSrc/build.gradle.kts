plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    kotlin("jvm") version "1.9.23"
}

repositories {
    mavenCentral()

    maven("https://maven.minecraftforge.net") {
        name = "Forge"
        content {
            includeGroup("net.minecraftforge")
            includeGroup("net.minecraftforge.gradle")
        }
    }
}

dependencies {
    implementation("net.minecraftforge.gradle:ForgeGradle:[6.0.18,6.2)")
}
