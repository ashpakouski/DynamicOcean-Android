package com.shpak.dynamicocean.di

import com.shpak.dynamicocean.repository.DummyTestRepository
import com.shpak.dynamicocean.repository.TestRepository
import com.shpak.dynamicocean.viewmodel.TestViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { DummyTestRepository() } bind TestRepository::class
    viewModelOf(::TestViewModel)
}