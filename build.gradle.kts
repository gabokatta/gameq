plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.diffplug.spotless") version "8.10.2"
    id("org.sonarqube") version "7.5.0.8588"
}

repositories { mavenCentral() }

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

dependencies {
    implementation("org.flywaydb:flyway-core:13.5.0")
    runtimeOnly("org.xerial:sqlite-jdbc:3.53.4.0")

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
}

spotless {
    java { googleJavaFormat("1.36.1").aosp() }
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
