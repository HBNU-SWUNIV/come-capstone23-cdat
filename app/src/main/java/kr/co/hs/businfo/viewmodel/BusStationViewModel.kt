package kr.co.hs.businfo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.hs.businfo.data.model.BusStationModel
import kr.co.hs.businfo.data.repository.BusStationRepositoryImpl
import kr.co.hs.businfo.domain.model.BusStation
import kr.co.hs.businfo.domain.repository.BusStationRepository

/**
 * @author hsbaewa@gmail.com
 * @since 2023/05/03
 * @comment AAC ViewModel - Android Architecture ViewModel
 * @param
 * @return
 **/
class BusStationViewModel(private val busStationRepository: BusStationRepository) : ViewModel() {
    private val _busStations = MutableLiveData<List<BusStation>>()
    val busStation: LiveData<List<BusStation>> by this::_busStations

    constructor() : this(BusStationRepositoryImpl())

    suspend fun getBusStationByUserLocation(
        latitude: Double,
        longitude: Double,
        radius: Double
    ): List<BusStation> {
        return withContext(Dispatchers.IO) {
            busStationRepository.getBusStationByUserLocation(latitude, longitude, radius)
        }
    }

    suspend fun getBusStationById(id: Int): List<BusStation> {
        return withContext(Dispatchers.IO) {
            busStationRepository.getBusStationById(id)
        }
    }
    suspend fun getBusStationByName(name: String): List<BusStation> {
        return withContext(Dispatchers.IO) {
            busStationRepository.searchBusStationByName(name)
        }
    }
    suspend fun getNearestBusStations(
        latitude: Double,
        longitude: Double,
        radius: Double,
        limit: Int
    ): List<BusStation> {
        return withContext(Dispatchers.IO) {
            busStationRepository.getBusStationByUserLocation(latitude, longitude, radius)
                .sortedBy { calculateDistance(latitude, longitude, it.stationLatitude, it.stationLongitude) }
                .take(limit)
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // 지구 반지름 (단위: km)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = R * c
        return distance
    }


    fun request() {

        viewModelScope.launch {
//            val busStationRepository = TestBusStationRepository()
            val result = busStationRepository.getBusStationById(1)

            _busStations.value = result
        }
    }
}


    // mock up
//    class TestBusStationRepository : BusStationRepository {
//        override suspend fun getBusStationById(id: Int): List<BusStation> {
//            return listOf(
//            )
//        }
//
//        override suspend fun searchBusStationByName(name: String): List<BusStation> {
//            TODO("Not yet implemented")
//        }
//
//        override suspend fun getBusStationByBusNum(id: Int): List<BusStation> {
//            TODO("Not yet implemented")
//        }
//
//
//
//    }
