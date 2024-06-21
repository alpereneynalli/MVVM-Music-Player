package com.player.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CoroutinesDispatchers @Inject constructor() {
    open val main: CoroutineDispatcher = Dispatchers.Main
    open val io: CoroutineDispatcher = Dispatchers.IO
    open val default: CoroutineDispatcher = Dispatchers.Default
}