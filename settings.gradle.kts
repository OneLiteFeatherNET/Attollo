rootProject.name = "Attollo"
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    versionCatalogs {
        create("libs") {
            // Libraries versions
            version("paper", "1.21.10-R0.1-SNAPSHOT")
            version("semver", "0.10.2")
            version("mockbukkit", "4.97.0")
            // Plugins versions
            version("publishdata", "1.4.0")
            version("modrinth", "2.+")
            version("hangar", "0.1.3")
            version("bukkit.yml", "0.6.0")
            version("paper.run", "2.3.1")
            version("shadow", "9.2.2")

            // Libraries
            library("paper", "io.papermc.paper", "paper-api").versionRef("paper")
            library("semver", "com.github.zafarkhaja", "java-semver").versionRef("semver")
            library("mockbukkit", "org.mockbukkit.mockbukkit", "mockbukkit-v1.21").versionRef("mockbukkit")

            // Plugins
            plugin("publishdata","de.chojo.publishdata").versionRef("publishdata")
            plugin("modrinth", "com.modrinth.minotaur").versionRef("modrinth")
            plugin("hangar", "io.papermc.hangar-publish-plugin").versionRef("hangar")
            plugin("bukkit.yml", "net.minecrell.plugin-yml.bukkit").versionRef("bukkit.yml")
            plugin("paper.run", "xyz.jpenilla.run-paper").versionRef("paper.run")
            plugin("shadow", "com.gradleup.shadow").versionRef("shadow")
        }
    }
}