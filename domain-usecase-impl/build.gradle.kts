plugins {
    id("dev.prompt.knob.io.convention.library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.domainUsecaseApi)
            implementation(projects.domainCore)
            implementation(projects.dataExecutorApi)
            implementation(projects.domainRepositoryApi)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }
        matching { it.name.startsWith("macos") && it.name.endsWith("Main") }.configureEach {
            dependencies { implementation(projects.dataMcpApi) }
        }
    }
}
