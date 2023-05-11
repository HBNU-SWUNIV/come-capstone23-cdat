package kr.co.hs.businfo.domain.usecase

import kr.co.hs.businfo.domain.model.BusStation
import kr.co.hs.businfo.domain.repository.BusStationRepository

class GetBusStationUsecase(private val busStationRepository: BusStationRepository) {

    suspend fun getBusStationById(id: Int): List<BusStation> {
        return busStationRepository.getBusStationById(id)
    }

    suspend fun searchBusStationByName(name: String): List<BusStation> {
        return busStationRepository.searchBusStationByName(name)
    }

    suspend fun getBusStationByBusNum(id: Int): List<BusStation> {
        return busStationRepository.getBusStationByBusNum(id)
    }

    suspend fun getBusStationByLocation(latitude: Double, longitude: Double, radius: Double): List<BusStation> {
        return busStationRepository.getBusStationByUserLocation(latitude, longitude, radius)
    }
}