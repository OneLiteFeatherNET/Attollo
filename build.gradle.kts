import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.0.2-SNAPSHOT"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    id("io.papermc.hangar-publish-plugin") version "0.0.5"
    id("com.modrinth.minotaur") version "2.+"
    id("org.jetbrains.changelog") version "2.0.0"

}

group = "dev.themeinerlp"
val baseVersion = "1.0.1"
val minecraftVersion = "1.19.4"
val supportedMinecraftVersions = listOf(
    "1.16.5",
    "1.17",
    "1.17.1",
    "1.18",
    "1.18.1",
    "1.18.2",
    "1.19",
    "1.19.1",
    "1.19.2",
    "1.19.3",
    "1.19.4"
)

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("dev.folia:folia-api:$minecraftVersion-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

bukkit {
    main = "dev.themeinerlp.attollo.Attollo"
    apiVersion = "1.16"
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
    supportedMinecraftVersions.forEach {
        register<RunServer>("run-$it") {
            minecraftVersion(it)
            jvmArgs("-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true")
            group = "run paper"
            runDirectory.set(file("run-$it"))
        }
    }
    register<RunServer>("runFolia") {
        downloadsApiService.set(xyz.jpenilla.runtask.service.DownloadsAPIService.folia(project))
        minecraftVersion(minecraftVersion)
        group = "run paper"
        runDirectory.set(file("run-folia"))
        jvmArgs("-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true")
    }
    generateBukkitPluginDescription {
        doLast {
            outputDirectory.file(fileName).get().asFile.appendText("folia-supported: true")
        }
    }
}

version = if (System.getenv().containsKey("CI")) {
    val finalVersion =
        if (System.getenv("GITHUB_REF_NAME") in listOf("main", "master") || System.getenv("GITHUB_REF_NAME")
                .startsWith("v")
        ) {
            baseVersion
        } else {
            baseVersion + "-SNAPSHOT+" + System.getenv("SHA_SHORT")
        }
    finalVersion
} else {
    baseVersion
}

changelog {
    version.set(baseVersion)
    path.set("${project.projectDir}/CHANGELOG.md")
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}

hangarPublish {
    if (System.getenv().containsKey("CI")) {
        publications.register("Attollo") {
            val finalVersion =
                if (System.getenv("GITHUB_REF_NAME") in listOf("main", "master") || System.getenv("GITHUB_REF_NAME")
                        .startsWith("v")
                ) {
                    "$baseVersion-RELEASE"
                } else {
                    baseVersion + "-SNAPSHOT+" + System.getenv("SHA_SHORT")
                }
            version.set(finalVersion)
            channel.set(System.getenv("HANGAR_CHANNEL"))
            changelog.set(project.changelog.renderItem(project.changelog.get(baseVersion)))
            apiKey.set(System.getenv("HANGAR_SECRET"))
            owner.set("OneLiteFeather")
            slug.set("Attollo")

            platforms {
                register(Platforms.PAPER) {
                    jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                    platformVersions.set(supportedMinecraftVersions)
                }
            }
        }
    }
}
if (System.getenv().containsKey("CI")) {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("ULt9SvKn")
        val finalVersion =
            if (System.getenv("GITHUB_REF_NAME") in listOf("main", "master") || System.getenv("GITHUB_REF_NAME")
                    .startsWith("v")
            ) {
                "$baseVersion-RELEASE"
            } else {
                baseVersion + "-SNAPSHOT+" + System.getenv("SHA_SHORT")
            }
        versionNumber.set(finalVersion)
        versionType.set(System.getenv("MODRINTH_CHANNEL"))
        uploadFile.set(tasks.shadowJar as Any)
        gameVersions.addAll(supportedMinecraftVersions)
        loaders.add("paper")
        loaders.add("bukkit")
        loaders.add("folia")
        changelog.set(project.changelog.renderItem(project.changelog.get(baseVersion)))
        dependencies { // A special DSL for creating dependencies
        }
    }
}