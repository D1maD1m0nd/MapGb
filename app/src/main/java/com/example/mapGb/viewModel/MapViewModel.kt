package com.example.mapGb.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapGb.model.local_store.LocationPoint
import com.example.mapGb.model.local_store.LocationPointDto
import com.example.mapGb.model.local_store.getDb
import com.example.mapGb.model.local_store.toDto
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(application: Application) : AndroidViewModel(application) {
    val locationLiveData = MutableLiveData<List<LocationPointDto>>()
    val pointVisibleLiveData = MutableLiveData<Boolean>()
    private val db  = getDb(application.applicationContext)

    private val dao = db.locationDao

    fun savePointData(name : String, annotation : String, point : Point?) {
        if(point != null) {
            viewModelScope.launch(Dispatchers.IO) {
                kotlin.runCatching {
                    val location = LocationPoint(name = name, annotation = annotation, lon =  point.longitude, lan = point.latitude)
                    dao.add(location)
                }.onSuccess {
                    withContext(Dispatchers.Main) {
                        pointVisibleLiveData.value = true
                    }
                }.onFailure {
                    Log.d("DBERROR", it.message ?: "")
                    withContext(Dispatchers.Main) {
                        pointVisibleLiveData.value = false
                    }
                }
            }
        }
    }

    fun getLocationList() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                dao.getLocationList()
            }.onSuccess { result ->
                val resultDto = result.map(LocationPoint :: toDto)
                withContext(Dispatchers.Main) {
                    locationLiveData.value = resultDto
                }
            }.onFailure {
                Log.d("DBERROR", it.message ?: "")
            }
        }
    }
}