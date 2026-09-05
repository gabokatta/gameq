plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

javafx {
    version = "25.0.4"
    modules("javafx.controls")
}

application {
    mainModule = "gameq"
    mainClass = "gameq.GameqApplication"
    applicationDefaultJvmArgs = listOf("--enable-native-access=javafx.graphics")
}
