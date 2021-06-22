
plugins {
    java
    id("io.freefair.lombok") version "5.3.0"
}

group = "org.tribot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "io.freefair.lombok")

    repositories {
        mavenCentral()
        // Tribot Central
        maven {
            setUrl("https://gitlab.com/api/v4/projects/20741387/packages/maven")
        }
    }

    dependencies {
        compileOnly("org.tribot:tribot-client:+")
    }
}