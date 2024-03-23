import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    id("org.ajoberstar.grgit") version "4.1.0"
    id("io.papermc.paperweight.userdev") version "1.5.10"
    `maven-publish`
}

group = "co.pvphub"
version = "1.0-SNAPSHOT"

// The Maven Publishing endpoint
val mavenEndpoint: PublishingEndpoint = PublishingEndpoint.PRIVATE

// Should we include the commit short in plugin.yml version
val containCommitInPluginVersionString = true

repositories {
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.pvphub.me/releases")
    maven {
        url = uri("https://maven.pvphub.me/private")
        credentials {
            username = System.getenv("PVPHUB_MAVEN_USERNAME")
            password = System.getenv("PVPHUB_MAVEN_SECRET")
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.3")

    compileOnly("com.mattmx:ktgui:2.2")

    compileOnly("co.pvphub:pvphub-core-api:1.3")
    compileOnly("co.pvphub:pvphub-core-backend:1.3")
    compileOnly("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.7.1")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets["main"].resources.srcDir("src/resources/")

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<ProcessResources> {
    val props = hashMapOf(
        "version" to if (containCommitInPluginVersionString) "$version-${getCheckedOutGitCommitHash()}" else version,
        "name" to rootProject.name
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

fun getCheckedOutGitCommitHash(): String = grgit.head().abbreviatedId

publishing {
    repositories {
        maven {
            name = "pvphub-$mavenEndpoint"
            url = uri("https://maven.pvphub.me/$mavenEndpoint")
            credentials {
                username = System.getenv("PVPHUB_MAVEN_USERNAME")
                password = System.getenv("PVPHUB_MAVEN_SECRET")
            }
        }
    }
    publications {
        create<MavenPublication>(rootProject.name) {
            from(components["java"])
            groupId = group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()
        }
    }
}

enum class PublishingEndpoint(
    private val id: String
) {
    PRIVATE("private"),
    PUBLIC("releases")
    ;

    override fun toString() = id
}
