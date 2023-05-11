package kr.co.hs.businfo.data.datasource

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Transaction
import kr.co.hs.businfo.data.model.BusStationModel

interface BusInfoRemoteSource {
    fun createStation(transaction: Transaction, busStation: BusStationModel)
    suspend fun getStations(busNumber: String): List<BusStationModel>
    suspend fun findStationByName(name: String): List<BusStationModel>
    suspend fun getStation(id: String): BusStationModel?
    suspend fun getStations(geoPoint: GeoPoint, radiusInMeters: Double): List<BusStationModel>
}