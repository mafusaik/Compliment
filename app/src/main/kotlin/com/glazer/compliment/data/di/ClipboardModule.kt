package com.glazer.compliment.data.di

import com.glazer.compliment.data.clipboard.SystemClipboard
import com.glazer.compliment.data.clipboard.SystemClipboardImpl
import org.koin.dsl.module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf


val clipboardModule = module {
    singleOf(::SystemClipboardImpl) {
        bind<SystemClipboard>()
    }
}
