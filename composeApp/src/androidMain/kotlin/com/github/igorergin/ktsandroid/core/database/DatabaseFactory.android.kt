package com.github.igorergin.ktsandroid.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.igorergin.ktsandroid.feature.repositories.data.local.AppDatabase

actual class DatabaseFactory(private val context: Context) {
    actual fun createBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbContext = context.applicationContext
        val dbFile = dbContext.getDatabasePath("github_app.db")

        return Room.databaseBuilder<AppDatabase>(
            context = dbContext,
            name = dbFile.absolutePath
        )
    }
}