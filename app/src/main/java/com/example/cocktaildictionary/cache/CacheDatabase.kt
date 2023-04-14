package com.example.cocktaildictionary.cache

import android.content.Context
import androidx.room.*

@Database(entities = [Cache::class], version = 1)

abstract class CacheDatabase: RoomDatabase() {

    abstract fun cacheDao() : CacheDao

    companion object{
        @Volatile
        private var INSTANCE : CacheDatabase? = null

        fun getDatabase(context: Context): CacheDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,CacheDatabase::class.java,"CacheDatabase"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}