rootProject.name = "Attollo"
pluginManagement {
    repositories {
        mavenCentral()
        maven("https://eldonexus.de/repository/maven-public/")
        gradlePluginPortal()
    }
}
includeBuild("build-logic")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("publishdata", "1.4.0")
            version("modrinth", "2.+")
            version("hangar", "0.1.3")
            version("bukkit.yml", "0.6.0")
            version("paper.run", "2.3.1")
            version("shadow", "8.1.7")
            version("paper", "1.21-R0.1-SNAPSHOT")
            version("adventure", "4.17.0")
            version("mockk", "1.13.11")
            version("mock.bukkit", "3.9.0")

            library("paper", "io.papermc.paper", "paper-api").versionRef("paper")
            library("adventure.minimessage", "net.kyori", "adventure-text-minimessage").versionRef("adventure")
            library("mockk", "io.mockk", "mockk").versionRef("mockk")
            library("mock.bukkit", "com.github.seeseemelk", "MockBukkit-v1.20").versionRef("mock.bukkit")

            plugin("publishdata","de.chojo.publishdata").versionRef("publishdata")
            plugin("modrinth", "com.modrinth.minotaur").versionRef("modrinth")
            plugin("hangar", "io.papermc.hangar-publish-plugin").versionRef("hangar")
            plugin("paper.yml", "net.minecrell.plugin-yml.paper").versionRef("paper.yml")
            plugin("paper.run", "xyz.jpenilla.run-paper").versionRef("paper.run")
            plugin("shadow", "com.gradleup.shadow").versionRef("shadow")
        }
    }
}