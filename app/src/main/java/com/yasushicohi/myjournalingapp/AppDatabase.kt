package com.yasushicohi.myjournalingapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(JournalingContent::class), version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): JournalingContentDao
}