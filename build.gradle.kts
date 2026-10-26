import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

val contractVersion: String = providers.gradleProperty("version").orNull
    ?.takeIf { it != "unspecified" }
    ?: "0.1.0"

group = "io.github.neronguyenvn.nerochat.contract"
version = contractVersion

subprojects {
    group = rootProject.group
    version = rootProject.version
    val parentName = project.parent?.name
    if (!parentName.isNullOrBlank()) {
        group = "io.github.neronguyenvn.nerochat.$parentName"
    }

    repositories {
        mavenCentral()
    }

    plugins.withId("java-library") {
        apply(plugin = "maven-publish")

        configure<JavaPluginExtension> {
            withSourcesJar()
        }

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("maven") {
                    from(components["java"])

                    pom {
                        name.set(project.name)
                        description.set("NeroChat Contract - ${project.name}")
                        url.set("https://github.com/neronguyen/KMP-ChatContract")
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                id.set("neronguyen")
                                name.set("Nero Nguyen")
                                email.set("neronguyenvn@gmail.com")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/neronguyen/KMP-ChatContract.git")
                            developerConnection.set("scm:git:ssh://github.com/neronguyen/KMP-ChatContract.git")
                            url.set("https://github.com/neronguyen/KMP-ChatContract")
                        }
                    }
                }
            }

            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/neronguyen/KMP-ChatContract")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR") ?: providers.gradleProperty("gpr.user").orNull
                        password = System.getenv("GITHUB_TOKEN") ?: providers.gradleProperty("gpr.key").orNull
                    }
                }
            }
        }
    }
}
