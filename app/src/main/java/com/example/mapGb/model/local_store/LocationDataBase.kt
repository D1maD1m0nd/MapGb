package com.example.mapGb.model.local_store

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



@Database(
    entities = [LocationPoint::class],
    version = 1
)
abstract class LocationDataBase: RoomDatabase() {
    abstract val locationDao: LocationDao
}

fun getDb(context: Context): LocationDataBase {
    return Room.databaseBuilder(context, LocationDataBase::class.java, NAME_DB)
        .fallbackToDestructiveMigration()
        .build()
}