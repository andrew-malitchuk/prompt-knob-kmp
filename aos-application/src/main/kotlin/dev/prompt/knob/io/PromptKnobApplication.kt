package dev.prompt.knob.io

import android.app.Application
import dev.prompt.knob.io.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Custom [Application] subclass for the Android target.
 *
 * Bootstraps the Koin dependency graph via [initKoin].
 */
class PromptKnobApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@PromptKnobApplication)
        }
    }
}
