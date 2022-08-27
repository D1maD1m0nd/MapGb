package com.example.mapGb.model.local_store

import androidx.room.*

@Dao
interface LocationDao {
    @Query("SELECT * FROM $LOCATION_TABLE_NAME")
    fun getLocationList(): List<LocationPoint>

    @Upsert()
    fun add(product: LocationPoint)
}