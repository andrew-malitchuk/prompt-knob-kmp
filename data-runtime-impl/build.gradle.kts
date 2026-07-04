plugins {
    id("dev.prompt.knob.io.convention.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.dataRuntimeApi)
            implementation(projects.dataCore)
            implementation(libs.koin.core)
        }
    }
}
