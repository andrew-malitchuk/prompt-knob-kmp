plugins {
    id("dev.prompt.knob.io.convention.feature")
    id("dev.prompt.knob.io.convention.di")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.orbit.core)
            implementation(libs.orbit.viewmodel)
            implementation(libs.orbit.compose)
            implementation(compose.material3)

            implementation(projects.domainCore)
            implementation(projects.domainUsecaseApi)
            implementation(projects.dataExecutorApi)
            implementation(projects.presentationCoreLocalisation)
            implementation(projects.presentationCoreNavigationApi)
            implementation(projects.presentationCoreStyling)
            implementation(projects.presentationCoreUi)
        }
        // filekit-dialogs-compose has no macOS variant — add it only to the platforms that ship it.
        androidMain.dependencies { implementation(libs.filekit.dialogs.compose) }
        desktopMain.dependencies { implementation(libs.filekit.dialogs.compose) }
        matching { it.name.startsWith("ios") && it.name.endsWith("Main") }.configureEach {
            dependencies { implementation(libs.filekit.dialogs.compose) }
        }
    }
}
