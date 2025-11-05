import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    kotlin("jvm") version "2.2.20"
    alias(libs.plugins.shadow)
    alias(libs.plugins.paper.run)
    alias(libs.plugins.bukkit.yml)
    alias(libs.plugins.hangar)
    alias(libs.plugins.modrinth)
    `maven-publish`
}

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
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-text-minimessage:4.24.0")
    implementation("com.github.zafarkhaja:java-semver:0.10.2")

    // testing
    testImplementation(kotlin("test"))
    testImplementation("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.19:3.1.0")
    testImplementation("io.mockk:mockk:1.14.6")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
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
        register("attollo.update") {
            description = "Allows the player to see update notifications"
            default = Default.OP
        }
    }
    commands {
        register("attollo") {
            permission = "attollo.command.attollo"
        }
    }
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

val baseVersion = version as String
val baseChannel = with(baseVersion) {
    when {
        contains("SNAPSHOT", true) -> "Snapshot"
        contains("ALPHA", true) -> "Alpha"
        contains("BETA", true) -> "Beta"
        else -> "Release"
    }
}
val changelogContent = "See [GitHub](https://github.com/OneLiteFeatherNET/Attollo/releases/tag/$baseVersion) for release notes."
hangarPublish {
    publications.register("Attollo") {
        version.set(baseVersion)
        channel.set(baseChannel)
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
    versionType.set(baseChannel.lowercase())
    versionNumber.set(baseVersion)
    versionName.set(baseVersion)
    changelog.set(changelogContent)
    changelog.set(changelogContent)
    uploadFile.set(tasks.shadowJar.flatMap { it.archiveFile })
    gameVersions.addAll(supportedMinecraftVersions)
    loaders.add("paper")
    loaders.add("bukkit")
    loaders.add("folia")
}