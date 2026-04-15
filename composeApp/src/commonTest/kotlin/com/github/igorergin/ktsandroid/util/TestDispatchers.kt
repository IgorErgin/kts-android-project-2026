package com.github.igorergin.ktsandroid.util

import com.github.igorergin.ktsandroid.core.util.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class TestDispatchers(
    val testDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
) : AppDispatchers {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}
