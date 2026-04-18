plugins {
    id("dev.prompt.knob.io.convention.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.domainUsecaseApi)
            implementation(projects.domainCore)
            implementation(projects.domainRepositoryApi)
            implementation(libs.koin.core)
        }
    }
}
