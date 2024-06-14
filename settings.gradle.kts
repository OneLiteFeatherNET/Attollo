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
            version("hangar", "0.1.2")
            version("bukkit.yml", "0.6.0")
            version("paper.run", "2.3.0")

            plugin("publishdata","de.chojo.publishdata").versionRef("publishdata")
            plugin("modrinth", "com.modrinth.minotaur").versionRef("modrinth")
            plugin("hangar", "io.papermc.hangar-publish-plugin").versionRef("hangar")
            plugin("bukkit.yml", "net.minecrell.plugin-yml.bukkit").versionRef("bukkit.yml")
            plugin("paper.run", "xyz.jpenilla.run-paper").versionRef("paper.run")
        }
    }
}