
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default
plugins {
    kotlin("jvm") version "1.8.10"
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
    compileOnly("com.destroystokyo.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
}

kotlin {
    /*sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }*/
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

bukkit {
    main = "com.github.themeinerlp.attollo.Attollo"
    apiVersion = "1.16.5"
    authors = listOf("TheMeinerLP")

    permissions {
        register("attollo.use") {
            description = "Allows the player to use the plugin"
            default = Default.TRUE
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            useK2 = true
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