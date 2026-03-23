package com.github.igorergin.ktsandroid.feature.repositories.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RepositoryDao {

    @Query("SELECT * FROM repositories")
    suspend fun getAllRepositories(): List<RepositoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<RepositoryEntity>)

    @Query("DELETE FROM repositories")
    suspend fun clearAll()
}