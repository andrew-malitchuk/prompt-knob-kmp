package dev.prompt.knob.io.convention.source.convention.feature

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import dev.prompt.knob.io.convention.core.ext.libs
import dev.prompt.knob.io.convention.core.ext.moduleName
import dev.prompt.knob.io.convention.source.convention.base.BaseConventionPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Convention plugin for UI-layer feature modules (`presentation-feature-*`).
 *
 * Applies the AGP 9.0 `com.android.kotlin.multiplatform.library` plugin together with
 * KMP and Compose Multiplatform plugins. Registers Android, iOS, macOS, and Desktop JVM
 * targets and wires the full Compose + coroutines + lifecycle dependency set to `commonMain`.
 *
 * Feature modules differ from [ApplicationConventionPlugin] in that each module's
 * iOS framework `baseName` is derived from [moduleName] rather than being hardcoded.
 * Feature modules differ from [LibraryConventionPlugin] in that they include Compose
 * dependencies and enable `androidResources`.
 *
 * @see LibraryConventionPlugin
 * @see ApplicationConventionPlugin
 */
class FeatureConventionPlugin : BaseConventionPlugin() {

    /**
     * Applies KMP, Android multiplatform library, Compose Multiplatform,
     * and Compose Compiler plugins.
     */
    override fun Project.configurePlugin() = with(project.pluginManager) {
        apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
        apply(libs.findPlugin("androidMultiplatformLibrary").get().get().pluginId)
        apply(libs.findPlugin("composeMultiplatform").get().get().pluginId)
        apply(libs.findPlugin("composeCompiler").get().get().pluginId)
    }

    /**
     * Configures the Android KMP library target with SDK versions, `androidResources`,
     * and JVM 21 compiler options.
     *
     * Uses [KotlinMultiplatformAndroidLibraryTarget] — the AGP 9.0 DSL replacement for
     * the legacy `android {}` block.
     */
    override fun Project.configureAndroidPlatform() {
        val minSdkVersion = libs.findVersion("minSdk").get().requiredVersion.toInt()
        val targetSdk = libs.findVersion("targetSdk").get().requiredVersion.toInt()
        extensions.getByType<KotlinMultiplatformExtension>().apply {
            targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
                namespace = moduleName
                compileSdk = targetSdk
                minSdk = minSdkVersion
                androidResources { enable = true }
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }
        }
    }

    /**
     * Registers iOS targets with a static framework whose `baseName` is derived from [moduleName].
     *
     * NOTE: `androidTarget()` is registered automatically by the
     * `com.android.kotlin.multiplatform.library` plugin and must not be called manually.
     */
    override fun Project.configureIOsPlatform() {
        extensions.getByType<KotlinMultiplatformExtension>().apply {
            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = moduleName.replace(".", "-")
                    isStatic = true
                }
            }
        }
    }

    /**
     * Registers macOS (x64, arm64) and Desktop JVM targets for the feature module.
     */
    override fun Project.configureDesktopPlatform() {
        extensions.getByType<KotlinMultiplatformExtension>().apply {
            macosX64()
            macosArm64()
            jvm("desktop")
        }
    }

    /**
     * Adds Compose runtime, foundation, material, UI, resources, UI tooling preview,
     * coroutines, and lifecycle libraries to `commonMain`.
     *
     * Enforces explicit API mode for all declarations in this module.
     *
     * @see <a href="https://slack-chats.kotlinlang.org/t/23173883/web-target-it-works-with-ios-android-desktop-but-now-my-wasm">Slack discussion on Compose multiplatform target wiring</a>
     */
    override fun Project.configureCommonDependencies() {
        val composeDependencies = extensions.getByType<ComposeExtension>().dependencies
        extensions.getByType<KotlinMultiplatformExtension>().apply {
            explicitApi()
            sourceSets.apply {
                commonMain.dependencies {
                    implementation(composeDependencies.runtime)
                    implementation(composeDependencies.foundation)
                    implementation(composeDependencies.material)
                    implementation(composeDependencies.ui)
                    implementation(composeDependencies.components.resources)
                    implementation(composeDependencies.components.uiToolingPreview)

                    implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                    implementation(libs.findLibrary("androidx-lifecycle-viewmodel").get())
                    implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
                }
            }
        }
    }

    /**
     * Adds Compose UI tooling to `androidRuntimeClasspath` for Android Studio preview support.
     *
     * NOTE: `androidRuntimeClasspath` is the correct configuration name for the
     * `com.android.kotlin.multiplatform.library` plugin — `debugImplementation` is
     * not available in the KMP Android library DSL.
     */
    override fun Project.configureAndroidDependencies() {
        val composeDependencies = extensions.getByType<ComposeExtension>().dependencies
        project.dependencies.add("androidRuntimeClasspath", composeDependencies.uiTooling)
    }

    /**
     * Adds `compose.desktop.currentOs` and `kotlinx-coroutines-swing` to `desktopMain`.
     *
     * `compose.desktop.currentOs` provides the platform-specific Compose Desktop renderer.
     * `kotlinx-coroutines-swing` supplies the Swing-aware coroutine dispatcher needed for
     * correct coroutine behaviour on the Desktop JVM target.
     */
    override fun Project.configureDesktopDependencies() {
        val composeDependencies = extensions.getByType<ComposeExtension>().dependencies
        extensions.getByType<KotlinMultiplatformExtension>().apply {
            sourceSets.getByName("desktopMain").dependencies {
                implementation(composeDependencies.desktop.currentOs)
                implementation(libs.findLibrary("kotlinx-coroutines-swing").get())
            }
        }
    }
}
