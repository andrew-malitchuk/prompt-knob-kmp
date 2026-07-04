package data.database.impl.core.builder

import data.database.impl.core.database.AppDatabase
import org.koin.core.module.Module

internal expect fun Module.provideDatabaseBuilder()
