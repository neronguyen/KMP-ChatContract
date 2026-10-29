plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(projects.contract.common)
    api(projects.validation.chat)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(projects.contract.auth)
    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}
