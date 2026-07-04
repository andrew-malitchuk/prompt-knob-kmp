plugins {
    id("dev.prompt.knob.io.convention.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.commonCore)
            implementation(projects.dataCore)
            implementation(projects.dataPreferenceApi)
            implementation(projects.dataDatabaseApi)
            implementation(projects.dataBleApi)
            implementation(projects.dataExecutorApi)
            implementation(projects.dataRuntimeApi)
            implementation(projects.domainCore)
            implementation(projects.domainRepositoryApi)
            implementation(libs.koin.core)
            implementation(libs.kermit)
            implementation(libs.kotlinx.datetime)
        }
    }
}
