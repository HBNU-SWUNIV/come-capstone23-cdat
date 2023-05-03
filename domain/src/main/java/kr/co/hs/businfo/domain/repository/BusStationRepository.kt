package kr.co.hs.businfo.domain.repository

import kr.co.hs.businfo.domain.model.BusStation

interface BusStationRepository {
    suspend fun getBusStationById(id: Int): List<BusStation>
    suspend fun searchBusStationByName(name: String): List<BusStation>
    suspend fun getBusStationByBusNum(id: Int): List<BusStation>
}