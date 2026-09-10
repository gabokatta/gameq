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
    // desktop
    implementation("io.github.mkpaz:atlantafx-base:2.1.0")
    implementation(platform("org.kordamp.ikonli:ikonli-bom:12.4.0"))
    implementation("org.kordamp.ikonli:ikonli-feather-pack")
    implementation("org.kordamp.ikonli:ikonli-javafx")

    // database
    implementation("org.flywaydb:flyway-core:13.5.0")
    runtimeOnly("org.xerial:sqlite-jdbc:3.53.4.0")

    // logging
    implementation("org.slf4j:slf4j-api:2.0.19")
    runtimeOnly("ch.qos.logback:logback-classic:1.6.3")

    // test
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
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

val jpackageInput = layout.buildDirectory.dir("jpackage/input")
val prepareJpackageInput =
    tasks.register<Sync>("prepareJpackageInput") {
        dependsOn(tasks.jar)
        from(tasks.jar)
        from(configurations.runtimeClasspath)
        into(jpackageInput)
    }

val java25 = javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(25) }
val jpackageDirectory = layout.buildDirectory.dir("jpackage")

tasks.register<Exec>("packageApp") {
    group = "distribution"
    description = "Builds a self-contained macOS application image."
    dependsOn(prepareJpackageInput)
    inputs.dir(jpackageInput)
    outputs.dir(jpackageDirectory.map { it.dir("GameQ.app") })

    doFirst {
        jpackageDirectory.get().dir("GameQ.app").asFile.deleteRecursively()
        executable(java25.get().metadata.installationPath.file("bin/jpackage"))
        args(
            "--type",
            "app-image",
            "--name",
            "GameQ",
            "--app-version",
            "1.0.0",
            "--vendor",
            "gameq",
            "--mac-package-identifier",
            "dev.gameq.app",
            "--dest",
            jpackageDirectory.get().asFile.absolutePath,
            "--input",
            jpackageInput.get().asFile.absolutePath,
            "--main-jar",
            tasks.jar.get().archiveFileName.get(),
            "--main-class",
            application.mainClass.get(),
            "--add-modules",
            "java.desktop,java.logging,java.management,java.naming,java.sql,java.xml,jdk.unsupported",
            "--java-options",
            "--enable-native-access=ALL-UNNAMED",
        )
    }
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
