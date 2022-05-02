val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val jetbrains_exposed_version: String by project
val sqllite_jdbc_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.4.10"
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // exposed
    implementation("org.jetbrains.exposed", "exposed-core", jetbrains_exposed_version)
    implementation("org.jetbrains.exposed", "exposed-dao", jetbrains_exposed_version)
    implementation("org.jetbrains.exposed", "exposed-jdbc", jetbrains_exposed_version)
    implementation("org.jetbrains.exposed", "exposed-java-time", jetbrains_exposed_version)
    implementation("org.xerial", "sqlite-jdbc", sqllite_jdbc_version)
    implementation("io.ktor:ktor-server-webjars:2.0.0-beta-1")
    implementation("org.webjars:jquery:3.2.1")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}