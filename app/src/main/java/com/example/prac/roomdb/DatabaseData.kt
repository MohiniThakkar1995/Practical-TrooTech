package com.example.jetpackdemo.roomdb

import android.content.Context
import android.os.AsyncTask
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
@Database(entities = arrayOf(Data::class), version = 1)
abstract class DatabaseData : RoomDatabase() {
    abstract fun studentDao():DAO

    companion object {
        private var INSTANCE: DatabaseData? = null
        fun getInstance(context: Context): DatabaseData {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    DatabaseData::class.java,
                    "roomdb")
                    .build()
            }
            return INSTANCE as DatabaseData
        }
    }
}