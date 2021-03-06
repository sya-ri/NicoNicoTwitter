plugins {
    kotlin("jvm") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = "me.syari.niconico.twitter"
version = "1.2.0"

repositories {
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx/")
    maven(url = "https://dl.bintray.com/nephyproject/stable")
}

dependencies {
    implementation("blue.starry:penicillin:6.0.5")
    implementation("io.ktor:ktor-client-cio:1.5.2")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
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

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("${project.name}-${project.version}.jar")
}
