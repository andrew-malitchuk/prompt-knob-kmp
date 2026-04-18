plugins {
    id("dev.prompt.knob.io.convention.feature")
}

compose.resources {
    publicResClass = true
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}
