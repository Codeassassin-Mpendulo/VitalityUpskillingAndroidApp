package com.example.cocktaildictionary.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToCache(cache: Cache)


    @Query("SELECT * FROM Cache ORDER BY time ASC")
    fun mostRecentData(): List<Cache>


    @Query("DELETE FROM Cache")
    suspend fun deleteAll()




}