package com.example.pants.domain.di

import com.example.pants.domain.useCases.CheckBoardOrderUseCase
import com.example.pants.domain.useCases.GetColorBoardUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::GetColorBoardUseCase)
    factoryOf(::CheckBoardOrderUseCase)
}
