plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(projects.validation.common)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
