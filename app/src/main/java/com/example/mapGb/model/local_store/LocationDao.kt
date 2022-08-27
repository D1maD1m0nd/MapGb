package com.example.mapGb.model.local_store

import androidx.room.*

@Dao
interface LocationDao {
    @Query("SELECT * FROM $NAME_DB")
    fun getLocationList(): List<LocationPoint>

    @Upsert()
    fun add(product: LocationPoint)
}