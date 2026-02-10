import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.URI

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.slackow"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    jar {
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to "ninbotfixer.Corrector", //will make your jar (produced by jar task) runnable
                    "ImplementationTitle" to project.name,
                    "Implementation-Version" to project.version)
            )
        }
    }
    shadowJar {
        manifest.inheritFrom(jar.get().manifest) //will make your shadowJar (produced by jar task) runnable
    }
}

tasks.test {
    useJUnitPlatform()
}

