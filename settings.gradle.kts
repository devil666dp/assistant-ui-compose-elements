pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AssistantUICompose"
if (providers.gradleProperty("webOnly").orNull != "true") {
    include(":app")
}
include(":webApp")