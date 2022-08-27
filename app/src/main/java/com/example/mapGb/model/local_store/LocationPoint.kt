package com.example.mapGb.model.local_store

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = LOCATION_TABLE_NAME)
data class LocationPoint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String? = null,
    val annotation: String? = null,
    val lan: Double? = null,
    val lon: Double? = null,
)

fun LocationPoint.toDto() : LocationPointDto {
    return LocationPointDto(id = this.id,
        name = this.name ?: "",
        annotation = this.annotation ?: "",
        lan = lan ?: 0.0,
        lon = lon ?: 0.0)
}

fun LocationPointDto.toLocation() : LocationPoint {
    return LocationPoint(id = this.id,
        name = this.name,
        annotation = this.annotation,
        lan = lan,
        lon = lon)
}