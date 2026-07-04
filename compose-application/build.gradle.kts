import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("dev.prompt.knob.io.convention.application")
    id("dev.prompt.knob.io.convention.di")
}

kotlin {
    listOf(macosX64(), macosArm64()).forEach { macosTarget ->
        macosTarget.binaries.executable {
            entryPoint = "dev.prompt.knob.io.source.entry.main"
            baseName = "PromptKnob"
        }
    }

    afterEvaluate {
        listOf("macosArm64", "macosX64").forEach { target ->
            listOf("Debug", "Release").forEach { buildType ->
                val linkTask = tasks.findByName("link${buildType}Executable${target.replaceFirstChar { it.uppercaseChar() }}") ?: return@forEach
                val aggregateTask = tasks.findByName("${target}AggregateResources") ?: return@forEach
                // Bundle the .kexe into a .app so NSBundle.mainBundle resolves resources correctly.
                // DefaultMacOsResourceReader looks at: NSBundle.mainBundle.resourcePath + "/compose-resources/" + path
                val kexeFile = layout.buildDirectory.file("bin/$target/${buildType.lowercase()}Executable/PromptKnob.kexe")
                val aggregatedDir = layout.buildDirectory.dir("kotlin-multiplatform-resources/aggregated-resources/$target")
                val appOutputDir = layout.buildDirectory.dir("bin/$target/${buildType.lowercase()}App/PromptKnob.app")
                val macosMainResourcesDir = layout.projectDirectory.dir("src/macosMain/resources")
                val bundleTask = tasks.register("bundle${buildType}AppFor${target.replaceFirstChar { it.uppercaseChar() }}") {
                    dependsOn(linkTask, aggregateTask)
                    inputs.file(kexeFile)
                    inputs.dir(aggregatedDir)
                    outputs.dir(appOutputDir)
                    doLast {
                        val app = appOutputDir.get().asFile
                        val macos = File(app, "Contents/MacOS").also { it.mkdirs() }
                        val appResources = File(app, "Contents/Resources").also { it.mkdirs() }
                        val resources = File(appResources, "compose-resources").also { it.mkdirs() }
                        kexeFile.get().asFile.copyTo(File(macos, "PromptKnob"), overwrite = true)
                        File(macos, "PromptKnob").setExecutable(true)
                        aggregatedDir.get().asFile.copyRecursively(resources, overwrite = true)
                        // Copy app icon into the bundle
                        val iconSrc = File(macosMainResourcesDir.asFile, "AppIcon.icns")
                        if (iconSrc.exists()) {
                            iconSrc.copyTo(File(appResources, "AppIcon.icns"), overwrite = true)
                        }
                        File(app, "Contents/Info.plist").writeText("""
                            <?xml version="1.0" encoding="UTF-8"?>
                            <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                            <plist version="1.0"><dict>
                                <key>CFBundleExecutable</key><string>PromptKnob</string>
                                <key>CFBundleIdentifier</key><string>dev.prompt.knob.io</string>
                                <key>CFBundleName</key><string>PromptKnob</string>
                                <key>CFBundleIconFile</key><string>AppIcon</string>
                                <key>CFBundlePackageType</key><string>APPL</string>
                                <key>CFBundleVersion</key><string>1.0</string>
                                <key>NSBluetoothAlwaysUsageDescription</key><string>PromptKnob uses Bluetooth to connect to your knob device and receive commands.</string>
                                <key>NSHighResolutionCapable</key><true/>
                            </dict></plist>
                        """.trimIndent())
                        val entitlements = File(app.parentFile, "PromptKnob.entitlements")
                        entitlements.writeText("""
                            <?xml version="1.0" encoding="UTF-8"?>
                            <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                            <plist version="1.0"><dict>
                                <key>com.apple.security.device.bluetooth</key><true/>
                            </dict></plist>
                        """.trimIndent())
                        ProcessBuilder(
                            "codesign", "--force", "--deep", "--sign", "-",
                            "--entitlements", entitlements.absolutePath,
                            app.absolutePath
                        ).inheritIO().start().waitFor()
                    }
                }
                linkTask.finalizedBy(bundleTask)

                val dmgOutputDir = layout.buildDirectory.dir("bin/$target/${buildType.lowercase()}Dmg")
                tasks.register("package${buildType}DmgFor${target.replaceFirstChar { it.uppercaseChar() }}") {
                    dependsOn(bundleTask)
                    inputs.dir(appOutputDir)
                    outputs.dir(dmgOutputDir)
                    doLast {
                        val dmgDir = dmgOutputDir.get().asFile.also { it.mkdirs() }
                        val dmgFile = File(dmgDir, "PromptKnob.dmg")
                        if (dmgFile.exists()) dmgFile.delete()
                        ProcessBuilder(
                            "hdiutil", "create",
                            "-volname", "PromptKnob",
                            "-srcfolder", appOutputDir.get().asFile.absolutePath,
                            "-ov", "-format", "UDZO",
                            dmgFile.absolutePath
                        ).inheritIO().start().waitFor()
                    }
                }

                tasks.register("run${buildType}${target.replaceFirstChar { it.uppercaseChar() }}") {
                    dependsOn(bundleTask)
                    val appPath = appOutputDir
                    doLast {
                        ProcessBuilder("open", appPath.get().asFile.absolutePath)
                            .inheritIO()
                            .start()
                    }
                }
            }
        }
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(projects.domainCore)
            implementation(projects.dataPreferenceImpl)
            implementation(projects.dataBleImpl)
            implementation(projects.dataDatabaseImpl)
            implementation(projects.dataRuntimeImpl)
            implementation(projects.dataRepositoryImpl)
            implementation(projects.dataExecutorImpl)
            implementation(projects.domainUsecaseImpl)
            implementation(projects.presentationCoreLocalisation)
            implementation(projects.presentationCoreNavigationApi)
            implementation(projects.presentationCoreNavigationImpl)
            implementation(projects.presentationCoreStyling)
            implementation(projects.presentationCoreUi)
            implementation(projects.presentationFeatureAbout)
            implementation(projects.presentationFeatureDevices)
            implementation(projects.presentationFeatureDevice)
            implementation(projects.presentationFeatureHome)
            implementation(projects.presentationFeatureOnboarding)
            implementation(projects.presentationFeatureSettings)
            implementation(projects.presentationFeatureCommand)
            implementation(projects.presentationFeaturePreset)
            implementation(projects.presentationFeatureSplash)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

        matching { it.name.startsWith("macos") && it.name.endsWith("Main") }.configureEach {
            dependencies {
                implementation(projects.dataMcpImpl)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.prompt.knob.io.source.entry.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.prompt.knob.io"
            packageVersion = "1.0.0"
            macOS {
                iconFile.set(project.file("src/macosMain/resources/AppIcon.icns"))
            }
        }
    }
}
