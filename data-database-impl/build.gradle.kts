plugins {
    id("dev.prompt.knob.io.convention.library")
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.dataDatabaseApi)
            implementation(projects.dataCore)
            implementation(projects.commonCore)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

// Room KMP: KSP must be applied per-target
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
    add("kspMacosX64", libs.room.compiler)
    add("kspMacosArm64", libs.room.compiler)
}
