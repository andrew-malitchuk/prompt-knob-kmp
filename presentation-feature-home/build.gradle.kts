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

            implementation(projects.domainCore)
            implementation(projects.domainUsecaseApi)
            implementation(projects.presentationCoreLocalisation)
            implementation(projects.presentationCoreNavigationApi)
            implementation(projects.presentationCoreStyling)
            implementation(projects.presentationCoreUi)

        }
    }
}
