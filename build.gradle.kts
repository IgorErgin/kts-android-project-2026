plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")
}

val installGitHooks = tasks.register<Copy>("installGitHooks") {
    from(file("$rootDir/scripts/pre-commit"))
    into(file("$rootDir/.git/hooks"))

    fileMode = "775".toInt(8)
}


subprojects {
    tasks.matching { it.name == "preBuild" }.configureEach {
        dependsOn(installGitHooks)
    }
}

tasks.register("checkProject") {
    dependsOn(tasks.withType<io.gitlab.arturbosch.detekt.Detekt>())
}