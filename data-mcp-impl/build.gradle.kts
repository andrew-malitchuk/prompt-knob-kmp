plugins {
    id("dev.prompt.knob.io.convention.library")
    id("dev.prompt.knob.io.convention.di")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.dataMcpApi)
            implementation(projects.domainUsecaseApi)
            implementation(libs.koin.core)
        }
        matching { it.name.startsWith("macos") && it.name.endsWith("Main") }.configureEach {
            dependencies {
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.cio)
                implementation(libs.ktor.server.sse)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kermit)
            }
        }
    }
}
