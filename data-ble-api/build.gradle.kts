plugins {
    id("dev.prompt.knob.io.convention.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.dataCore)
            api(projects.domainCore)
        }
    }
}
