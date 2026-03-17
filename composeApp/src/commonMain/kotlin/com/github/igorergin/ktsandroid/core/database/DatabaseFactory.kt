package com.github.igorergin.ktsandroid.core.database

import androidx.room.RoomDatabase
import com.github.igorergin.ktsandroid.feature.repositories.data.local.AppDatabase


expect class DatabaseFactory {
    fun createBuilder(): RoomDatabase.Builder<AppDatabase>
}