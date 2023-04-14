package com.example.cocktaildictionary.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cache")
data class Cache(

    @PrimaryKey(autoGenerate = true) val id:Int?,
    val time: String,
    val latestData: String
)