package com.github.igorergin.ktsandroid.core.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class CrashlyticsAntilog : Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        if (priority < LogLevel.ERROR) return

        val crashlytics = FirebaseCrashlytics.getInstance()

        val fullMessage = if (tag != null) "[$tag] $message" else message ?: ""
        crashlytics.log(fullMessage)

        if (throwable != null) {
            crashlytics.recordException(throwable)
        } else {
            crashlytics.recordException(Exception(fullMessage))
        }
    }
}