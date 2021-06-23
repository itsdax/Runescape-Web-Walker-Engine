
plugins {
    java
    `maven-publish`
    id("io.freefair.lombok") version "5.3.0"
}

group = "org.tribot"
version = "1.0-SNAPSHOT"

val baseDir = projectDir

repositories {
    mavenCentral()
    // Tribot Central
    maven {
        setUrl("https://gitlab.com/api/v4/projects/20741387/packages/maven")
    }
}

dependencies {
    compileOnly("org.tribot:tribot-client:+")
    compileOnly(files("${baseDir.absolutePath}/allatori-annotations-7.5.jar"))
}

publishing {
    publications {
        val mavenJava by publications.creating(MavenPublication::class) {
            from(components.getByName("java"))
            pom {
                artifactId = "tribot-dax-walker"
                name.set("Tribot Dax Walker Engine")
                description.set("Tribot's fork of DaxWalker Engine")
                url.set("https://tribot.org/")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
            }
        }
    }
    repositories {
        // Tribot Central
        maven {
            setUrl("https://gitlab.com/api/v4/projects/20741387/packages/maven")
            credentials(HttpHeaderCredentials::class) {
                name = "Deploy-Token"
                value = if (project.hasProperty("tribotDeployToken"))
                    project.property("tribotDeployToken") as String
                else ""
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
}