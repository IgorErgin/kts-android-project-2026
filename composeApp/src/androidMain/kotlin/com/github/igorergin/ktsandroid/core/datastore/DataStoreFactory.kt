package com.github.igorergin.ktsandroid.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createAndroidDataStore(context: Context, fileName: String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { context.filesDir.resolve("$fileName.preferences_pb").absolutePath.toPath() }
    )
}