import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    java
    antlr
    kotlin(module = "jvm") version "1.3.31"
    id("org.jetbrains.dokka") version "0.9.18"
    id("com.github.johnrengelman.shadow") version "2.0.4"
    maven
    `maven-publish`
    signing
}

group = "com.developersam"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
    maven(url = "http://dl.bintray.com/kotlin/kotlinx")
}

dependencies {
    compile(kotlin(module = "stdlib-jdk8"))
    antlr(dependencyNotation = "org.antlr:antlr4:4.5")
    implementation(dependencyNotation = "org.jetbrains.kotlinx:kotlinx-collections-immutable:0.1")
    implementation(dependencyNotation = "org.apache.commons:commons-text:1.6")
    testImplementation(kotlin(module = "reflect"))
    testImplementation(kotlin(module = "test"))
    testImplementation(kotlin(module = "test-junit"))
    testImplementation(dependencyNotation = "io.kotlintest:kotlintest-runner-junit5:3.1.10")
    testImplementation(dependencyNotation = "org.slf4j:slf4j-api:1.7.25")
    testImplementation(dependencyNotation = "org.slf4j:slf4j-simple:1.7.25")
}

tasks {
    withType<AntlrTask> {
        outputDirectory = file(path = "${project.rootDir}/src/main/java/vm/critter/parser/generated")
        arguments.addAll(listOf("-package", "vm.critter.parser.generated", "-no-listener", "-visitor"))
    }
    named<Test>(name = "test") {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    withType<KotlinJvmCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
    "compileJava" { dependsOn("generateGrammarSource") }
    "compileKotlin" { dependsOn("generateGrammarSource") }
    "assemble" { dependsOn(shadowJar) }
    shadowJar {
        manifest { attributes["Main-Class"] = "os.critter.Main" }
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

val compileKotlin: KotlinJvmCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinJvmCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
