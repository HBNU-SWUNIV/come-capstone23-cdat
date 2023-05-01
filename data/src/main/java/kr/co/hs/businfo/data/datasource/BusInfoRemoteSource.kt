package kr.co.hs.businfo.data.datasource

import kr.co.hs.businfo.data.model.BusStationModel

interface BusInfoRemoteSource {
    suspend fun getStations(busNumber: String): List<BusStationModel>
    suspend fun findStationByName(name: String): List<BusStationModel>
}