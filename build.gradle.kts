import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    kotlin("jvm") version "2.0.0"
    alias(libs.plugins.publishdata)
    alias(libs.plugins.shadow)
    alias(libs.plugins.paper.run)
    alias(libs.plugins.bukkit.yml)
    alias(libs.plugins.hangar)
    alias(libs.plugins.modrinth)
    id("olf.build-logic")
    `maven-publish`
}
if (!File("$rootDir/.git").exists()) {
    logger.lifecycle(
        """
    **************************************************************************************
    You need to fork and clone this repository! Don't download a .zip file.
    If you need assistance, consult the GitHub docs: https://docs.github.com/get-started/quickstart/fork-a-repo
    **************************************************************************************
    """.trimIndent()
    ).also { System.exit(1) }
}

group = "net.onelitefeather"
version = "1.4.0"

val minecraftVersion = "1.20.6"
val supportedMinecraftVersions = listOf(
    "1.20",
    "1.20.1",
    "1.20.2",
    "1.20.3",
    "1.20.4",
    "1.20.5",
    "1.20.6",
)

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")


    // testing
    testImplementation(kotlin("test"))
    testImplementation("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.19:3.1.0")
    testImplementation("io.mockk:mockk:1.13.13")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

bukkit {
    main = "dev.themeinerlp.attollo.Attollo"
    apiVersion = "1.20"
    authors = listOf("TheMeinerLP")
    foliaSupported = true

    permissions {
        register("attollo.use") {
            description = "Allows the player to use the plugin"
            default = Default.TRUE
        }
    }
    commands {
        register("attollo") {
            permission = "attollo.command.attollo"
        }
    }
}

publishData {
    useEldoNexusRepos(false)
    publishTask("shadowJar")
}


tasks {
    named<Jar>("jar") {
        archiveClassifier.set("unshaded")
    }
    named("build") {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("")
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    supportedMinecraftVersions.forEach { serverVersion ->
        register<RunServer>("run-$serverVersion") {
            minecraftVersion(serverVersion)
            jvmArgs("-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true")
            group = "run paper"
            runDirectory.set(file("run-$serverVersion"))
            pluginJars(rootProject.tasks.shadowJar.map { it.archiveFile }.get())
        }
    }
    register<RunServer>("runFolia") {
        downloadsApiService.set(xyz.jpenilla.runtask.service.DownloadsAPIService.folia(project))
        minecraftVersion(minecraftVersion)
        group = "run paper"
        runDirectory.set(file("run-folia"))
        jvmArgs("-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true")
    }
}

val branch = rootProject.branchName()
val baseVersion = publishData.getVersion(false)
val isRelease = !baseVersion.contains('-')
val isMainBranch = branch == "master"
if (!isRelease || isMainBranch) { // Only publish releases from the main branch
    val suffixedVersion =
        if (isRelease) baseVersion else baseVersion + "+" + System.getenv("GITHUB_RUN_NUMBER")
    val changelogContent = if (isRelease) {
        "See [GitHub](https://github.com/OneLiteFeatherNET/Attollo) for release notes."
    } else {
        val commitHash = rootProject.latestCommitHash()
        "[$commitHash](https://github.com/OneLiteFeatherNET/Attollo/commit/$commitHash) ${rootProject.latestCommitMessage()}"
    }
    hangarPublish {
        publications.register("Attollo") {
            version.set(suffixedVersion)
            channel.set(if (isRelease) "Release" else "Snapshot")
            changelog.set(changelogContent)
            apiKey.set(System.getenv("HANGAR_SECRET"))
            id.set("Attollo")

            platforms {
                register(Platforms.PAPER) {
                    jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                    platformVersions.set(supportedMinecraftVersions)
                }
            }
        }
    }
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("ULt9SvKn")
        versionType.set(if (isRelease) "release" else "beta")
        versionNumber.set(suffixedVersion)
        versionName.set(suffixedVersion)
        changelog.set(changelogContent)
        changelog.set(changelogContent)
        uploadFile.set(tasks.shadowJar.flatMap { it.archiveFile })
        gameVersions.addAll(supportedMinecraftVersions)
        loaders.add("paper")
        loaders.add("bukkit")
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        // Configure our maven publication
        publishData.configurePublication(this)
    }

    repositories {
        // We add EldoNexus as our repository. The used url is defined by the publish data.
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    // Those credentials need to be set under "Settings -> Secrets -> Actions" in your repository
                    username = System.getenv("ELDO_USERNAME")
                    password = System.getenv("ELDO_PASSWORD")
                }
            }

            name = "EldoNexus"
            setUrl(publishData.getRepository())
        }
    }
}