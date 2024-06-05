plugins {
    kotlin("jvm") version "1.9.23"
    id("io.github.goooler.shadow") version "8.1.7"
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

group = "com.ldhdev"
version = "0.1.0"

repositories {
    mavenCentral()

    maven("https://maven.enginehub.org/repo/")

    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    compileOnly("com.sk89q.worldedit:worldedit-core:7.4.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.4.0-SNAPSHOT")

    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"

        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }

    compileKotlin {
        kotlinOptions {
            javaParameters = true
        }
    }

    processResources {

        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    val reloadServer = register<Delete>("reloadServer") {
        delete(file("server/plugins/update/RELOAD"))
    }

    val copyJar = register<Copy>("copyJar") {
        val outDir = file("server/plugins")
        val jarFile = shadowJar.map { it.archiveFile.get().asFile }

        onlyIf { outDir.exists() }

        from(jarFile)
        into(outDir)

        finalizedBy(reloadServer)
    }

    jar {
        enabled = false

        dependsOn(shadowJar)
    }

    shadowJar {

        mergeServiceFiles()

        archiveClassifier.set("")
        configurations = listOf(project.configurations.runtimeClasspath.get())

        finalizedBy(copyJar)
    }
}