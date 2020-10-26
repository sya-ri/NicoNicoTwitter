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
    implementation("com.soywiz.korlibs.klock:klock-jvm:1.11.3")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}