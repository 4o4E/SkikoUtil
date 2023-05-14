import org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

dependencies {
    // apt
    kapt(project(":skiko-util-apt"))
    implementation(project(":skiko-util-apt"))
    // util
    api(project(":skiko-util-util"))
    api(project(":skiko-util-gif-codec"))
    api(project(":skiko-util-draw"))
    // skiko
    compileOnly(skiko("windows-x64"))
    compileOnly(skiko("linux-x64"))
    // serialization
    implementation(kotlinx("serialization-core-jvm", "1.3.3"))
    // kaml
    implementation("com.charleskorn.kaml:kaml:0.45.0")
    // reflect
    implementation(kotlin("reflect", Versions.kotlin))
    // test
    testImplementation(kotlin("test", Versions.kotlin))
    testImplementation(project(":skiko-util-util"))
}

tasks {
    test {
        useJUnit()
        workingDir = projectDir.resolve("run")
//        maxHeapSize = "8G"
//        minHeapSize = "8G"
    }

//    withType<KaptWithoutKotlincTask>().configureEach {
//        kaptProcessJvmArgs.add("-Xmx1G")
//    }
}