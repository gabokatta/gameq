plugins {
    application
    jacoco
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.diffplug.spotless") version "8.10.2"
    id("org.sonarqube") version "7.5.0.8588"
}

repositories { mavenCentral() }

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

dependencies {
    implementation("org.flywaydb:flyway-core:13.5.0")
    implementation("org.slf4j:slf4j-api:2.0.19")
    runtimeOnly("org.xerial:sqlite-jdbc:3.53.4.0")
    runtimeOnly("ch.qos.logback:logback-classic:1.6.3")

    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

javafx {
    version = "25.0.4"
    modules("javafx.controls")
}

application {
    mainClass = "gameq.Launcher"
    applicationDefaultJvmArgs = listOf("--enable-native-access=javafx.graphics,ALL-UNNAMED")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    systemProperty("gameq.flywayLogLevel", "WARN")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports { xml.required = true }
}

spotless {
    java { palantirJavaFormat("2.80.0") }
    kotlinGradle { ktfmt("0.51").kotlinlangStyle() }
    format("repository") {
        target("*.md", ".gitignore", ".editorconfig", ".github/**/*.yml")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

sonar {
    properties {
        property("sonar.projectKey", "gabokatta_gameq")
        property("sonar.organization", "gabokatta")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
