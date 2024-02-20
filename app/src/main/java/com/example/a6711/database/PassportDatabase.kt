package com.example.a6711.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.a6711.dao.PassportDao
import com.example.a6711.dao.RegionDao
import com.example.a6711.entitiy.PassportEntity
import com.example.a6711.entitiy.RegionEntity


@Database(entities = [PassportEntity::class, RegionEntity::class], version = 1, exportSchema = true)
abstract class PassportDatabase : RoomDatabase() {

    abstract fun passportDao(): PassportDao
    abstract fun regionDao(): RegionDao

    companion object{
        private var instance: PassportDatabase?=null

        @Synchronized
        fun getInstance(context: Context):PassportDatabase{
            if (instance == null){
                instance = Room.databaseBuilder(context,PassportDatabase::class.java,"passport_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }


}