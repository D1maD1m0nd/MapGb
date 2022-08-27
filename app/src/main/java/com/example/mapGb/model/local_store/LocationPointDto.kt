package com.example.mapGb.model.local_store

import com.yandex.mapkit.geometry.Point

data class LocationPointDto(val id: Int,
                            val name: String,
                            val annotation: String,
                            val point : Point)