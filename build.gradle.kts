plugins {
    id("fabric-loom") version "1.5-SNAPSHOT"
    `maven-publish`
    java
}

version = property("mod_version")!!
group = property("maven_group")!!

repositories {
    maven("https://maven.shurik.me/releases")
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    modImplementation(fabricApi.module("fabric-api-base", property("fabric_version").toString()))

    modImplementation("me.shurik.glimmer:glimmer-command-builder:0.1.0")
}

tasks {
    loom {
        accessWidenerPath = file("src/main/resources/simple-chunk-manager.accessWidener")
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(mutableMapOf("version" to project.version))
        }
    }

    val targetJavaVersion = 17
    java {
        val javaVersion = JavaVersion.toVersion(17)
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
        }

        // Add sources jar
        withSourcesJar()
    }

    jar {
        from("LICENSE") {
            rename { "${this}_${property("archives_base_name")}" }
        }
    }

    publishing {
        repositories {
            maven("https://maven.shurik.me/releases") {
                name = "shurikMaven"
                credentials {
                    username = property("shurikMeMavenUser").toString()
                    password = property("shurikMeMavenPass").toString()
                }
            }
        }

        publications {
            create<MavenPublication>("release") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }

                artifact(remapSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }
    }
}