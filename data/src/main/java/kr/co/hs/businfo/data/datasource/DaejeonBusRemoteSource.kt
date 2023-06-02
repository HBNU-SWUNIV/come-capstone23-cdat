package kr.co.hs.businfo.data.datasource

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Transaction
import kr.co.hs.businfo.data.model.BusArriveInfoModel
import kr.co.hs.businfo.data.model.BusRouteModel
import kr.co.hs.businfo.data.model.BusStopInfoModel

interface DaejeonBusRemoteSource {
    fun createStation(transaction: Transaction, model: BusRouteModel)
    suspend fun getStations(busNumber: String): List<BusStopInfoModel>
    suspend fun findStationByName(name: String): List<BusStopInfoModel>
    suspend fun getStation(stopId: Int): BusStopInfoModel?
    suspend fun getStations(geoPoint: GeoPoint, radiusInMeters: Double): List<BusStopInfoModel>
    suspend fun getRoute(routeId: Int): Set<BusStopInfoModel>
    suspend fun getArriveInfo(stopId: Int): List<BusArriveInfoModel>
}