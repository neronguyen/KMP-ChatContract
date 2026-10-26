pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "KMP-ChatContract"

include("contract:common")
include("contract:auth")
include("contract:chat")
include("validation:common")
include("validation:auth")
include("validation:chat")
