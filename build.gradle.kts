import com.github.jengelman.gradle.plugins.shadow.tasks.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
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

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "${project.group}.MainKt"
            )
        )
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("${project.name}-${project.version}.jar")
}
