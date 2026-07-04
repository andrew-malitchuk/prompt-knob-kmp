plugins {
    id("dev.prompt.knob.io.convention.library")
    id("dev.prompt.knob.io.convention.di")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.dataExecutorApi)
            implementation(projects.domainCore)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
