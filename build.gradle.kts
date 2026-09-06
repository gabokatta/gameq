plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.diffplug.spotless") version "8.10.2"
    id("org.sonarqube") version "7.5.0.8588"
}

repositories { mavenCentral() }

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

javafx {
    version = "25.0.4"
    modules("javafx.controls")
}

application {
    mainModule = "gameq"
    mainClass = "gameq.GameqApplication"
    applicationDefaultJvmArgs = listOf("--enable-native-access=javafx.graphics")
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
    }
}
