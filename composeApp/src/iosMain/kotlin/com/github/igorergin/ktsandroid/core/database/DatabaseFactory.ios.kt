package com.github.igorergin.ktsandroid.core.database

import androidx.room.RoomDatabase
import com.github.igorergin.ktsandroid.feature.repositories.data.local.AppDatabase

actual class DatabaseFactory {
    actual fun createBuilder(): RoomDatabase.Builder<AppDatabase> {
        TODO("Not yet implemented")
    }
}