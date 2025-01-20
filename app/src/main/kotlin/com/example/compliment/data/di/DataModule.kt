package com.example.compliment.data.di

import org.koin.dsl.module

val dataModule = module {
    includes(
        repositoryModule,
        roomModule,
        clipboardModule
    )
}