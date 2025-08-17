import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    kotlin("jvm") version "2.2.10"
    alias(libs.plugins.publishdata)
    alias(libs.plugins.shadow)
    alias(libs.plugins.paper.run)
    alias(libs.plugins.paper.yml)
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

val supportedMinecraftVersions = listOf(
    "1.20.6",
    "1.21"
)

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper)
    implementation(libs.adventure.minimessage)
    implementation("com.github.zafarkhaja:java-semver:0.10.2")

    // testing
    testImplementation(kotlin("test"))
    testImplementation(libs.paper)
    testImplementation(libs.mock.bukkit)
    testImplementation(libs.mockk)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

paper {
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