plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        resources {
            srcDir("src/main/asl")
        }
    }
}

dependencies {
    implementation("io.github.jason-lang:jason-interpreter:3.2.1") // Java 17
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.5")

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runParkingMain") {
    group = "run"
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("jason.infra.jade.RunJadeMAS")
    args(file("parkingMain.mas2j").absolutePath)
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
}

tasks.register<JavaExec>("runParkingContainer") {
    group = "run"
    val containerName = providers.gradleProperty("name").orNull
        ?: throw GradleException("Missing required property: -Pname=<container-name>")
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("jason.infra.jade.RunJadeMAS")
    args(
        file("parkingAgents.mas2j").absolutePath,
        "-container",
        "-container-name", containerName,
        "-host", "localhost"
    )
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
}
