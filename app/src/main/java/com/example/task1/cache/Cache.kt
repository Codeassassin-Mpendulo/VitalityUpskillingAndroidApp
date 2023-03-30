package com.example.task1.cache

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.task1.network.Cocktail
import com.example.task1.network.CocktailList

@Entity(tableName = "Cache")
data class Cache(

    @PrimaryKey(autoGenerate = true) val id:Int?,
    val time: String,
    val latestData: String
)