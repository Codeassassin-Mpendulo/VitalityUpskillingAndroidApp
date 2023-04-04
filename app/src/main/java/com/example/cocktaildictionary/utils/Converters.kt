package com.example.cocktaildictionary.utils

import androidx.room.TypeConverter
import com.example.cocktaildictionary.network.Cocktail
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters {
    @TypeConverter
    fun fromString(value: String?): List<Cocktail> {
        val listType: Type = object : TypeToken<List<Cocktail>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromCocktailList(list: List<Cocktail>): String {
        val gson = Gson()
        var json: String = gson.toJson(list)
        return json
    }
}