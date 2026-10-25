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

include("contract-core")
include("contract-auth")
include("contract-chat")
include("validation-core")
include("validation-auth")
include("validation-chat")
