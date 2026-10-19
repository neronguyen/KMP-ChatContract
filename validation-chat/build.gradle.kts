plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(project(":validation-core"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
