import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}

group = "me.syari.niconico.twitter"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/nephyproject/stable")
}

dependencies {
    implementation("blue.starry:penicillin:5.0.0")
    implementation("io.ktor:ktor-client-cio:1.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}