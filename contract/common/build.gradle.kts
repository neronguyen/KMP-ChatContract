plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
    dependsOn(
        ":contract:auth:compileKotlin",
        ":contract:chat:compileKotlin",
        ":validation:common:compileKotlin",
        ":validation:auth:compileKotlin",
        ":validation:chat:compileKotlin",
    )
}
