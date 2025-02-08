package com.glazer.compliment.data.di

import org.koin.dsl.module

val dataModule = module {
    includes(
        repositoryModule,
        roomModule,
        clipboardModule
    )
}