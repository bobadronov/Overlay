package org.bigblackowl.overlay.di

import org.bigblackowl.overlay.storage.DataStorage
import org.bigblackowl.overlay.storage.DesktopDataStorage
import org.bigblackowl.overlay.viewmodel.SettingsViewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule)
    }
}

val sharedModule = module {
    single<DataStorage> { DesktopDataStorage() }
    viewModel { SettingsViewModel(get()) }
}