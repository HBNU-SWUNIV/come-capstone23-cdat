package kr.co.hs.businfo.data.repository

import com.google.firebase.firestore.GeoPoint
import kr.co.hs.businfo.data.datasource.BusInfoRemoteSource
import kr.co.hs.businfo.data.datasource.impl.BusInfoRemoteSourceImpl
import kr.co.hs.businfo.data.mapper.BusStationMapper.toDomain
import kr.co.hs.businfo.domain.repository.BusStationRepository

class BusStationRepositoryImpl : BusStationRepository {

    private val busInfoRemoteSource: BusInfoRemoteSource by lazy { BusInfoRemoteSourceImpl() }

    override suspend fun getBusStationById(id: Int) =
        busInfoRemoteSource
            .getStation(id.toString())
            ?.toDomain()
            ?.let { model ->
                listOf(model)
            } ?: emptyList()

    override suspend fun searchBusStationByName(name: String) =
        busInfoRemoteSource
            .findStationByName(name)
            .mapNotNull { it.toDomain() }

    override suspend fun getBusStationByBusNum(id: Int) =
        busInfoRemoteSource
            .getStations(id.toString())
            .mapNotNull { it.toDomain() }

    override suspend fun getBusStationByUserLocation(
        latitude: Double,
        longitude: Double,
        radius: Double
    ) = busInfoRemoteSource
        .getStations(GeoPoint(latitude, longitude), radius)
        .mapNotNull { it.toDomain() }
}