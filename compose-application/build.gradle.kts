import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("dev.prompt.knob.io.convention.application")
    id("dev.prompt.knob.io.convention.di")
}

kotlin {
    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(projects.domainCore)
            implementation(projects.dataPreferenceImpl)
            implementation(projects.dataRepositoryImpl)
            implementation(projects.domainUsecaseImpl)
            implementation(projects.presentationCoreLocalisation)
            implementation(projects.presentationCoreNavigationApi)
            implementation(projects.presentationCoreNavigationImpl)
            implementation(projects.presentationCoreStyling)
            implementation(projects.presentationCoreUi)
            implementation(projects.presentationFeatureAbout)
            implementation(projects.presentationFeatureHome)
            implementation(projects.presentationFeatureOnboarding)
            implementation(projects.presentationFeatureSettings)
            implementation(projects.presentationFeatureSplash)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.prompt.knob.io.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.prompt.knob.io"
            packageVersion = "1.0.0"
        }
    }
}
