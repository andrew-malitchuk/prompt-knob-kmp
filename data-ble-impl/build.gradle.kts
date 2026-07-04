plugins {
    id("dev.prompt.knob.io.convention.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.dataBleApi)
            implementation(projects.dataCore)
            implementation(libs.koin.core)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.kable.core)
            implementation(libs.koin.android)
        }
        // iosMain and macosMain are created lazily by KGP's default hierarchy; add Kable
        // to all Apple target source sets (both platforms use CoreBluetooth via Kable).
        matching { (it.name.startsWith("ios") || it.name.startsWith("macos")) && it.name.endsWith("Main") }.configureEach {
            dependencies {
                implementation(libs.kable.core)
            }
        }
    }
}
