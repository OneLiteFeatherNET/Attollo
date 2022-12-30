import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

group = "dev.themeinerlp"
val baseVersion = "1.0.0"
val minecraftVersion = "1.16.5"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    // compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly("com.destroystokyo.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

bukkit {
    main = "com.github.themeinerlp.attollo.Attollo"
    apiVersion = "1.17"
    authors = listOf("TheMeinerLP")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    runServer {
        minecraftVersion(minecraftVersion)
    }
}

version = if (System.getenv().containsKey("CI")) {
    val releaseOrSnapshot = if (System.getenv("CI_COMMIT_BRANCH").equals("main", true)) {
        ""
    } else if(System.getenv("CI_COMMIT_BRANCH").equals("test", true)) {
        "-PREVIEW"
    } else {
        "-SNAPSHOT"
    }
    "$baseVersion$releaseOrSnapshot+${System.getenv("CI_COMMIT_SHORT_SHA")}"
} else {
    "$baseVersion-SNAPSHOT"
}