package com.example.pants.di

import com.example.pants.data.di.networkModule
import com.example.pants.domain.di.productRepositoryModule
import com.example.pants.domain.di.useCaseModule
import org.koin.dsl.module

val dataModule = module {
    includes(networkModule, productRepositoryModule, useCaseModule)
}
