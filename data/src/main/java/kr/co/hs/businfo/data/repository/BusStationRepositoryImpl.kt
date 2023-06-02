package kr.co.hs.businfo.data.repository

import com.google.firebase.firestore.GeoPoint
import kr.co.hs.businfo.data.datasource.impl.DaejeonBusRemoteSourceImpl
import kr.co.hs.businfo.data.mapper.BusStationMapper.toDomain
import kr.co.hs.businfo.domain.repository.BusStationRepository

class BusStationRepositoryImpl : BusStationRepository {

    private val source = DaejeonBusRemoteSourceImpl("")

    override suspend fun getBusStationById(id: Int) =
        source
            .getStation(id)
            ?.toDomain()
            ?.let { model ->
                listOf(model)
            } ?: emptyList()

    override suspend fun searchBusStationByName(name: String) =
        source
            .findStationByName(name)
            .mapNotNull { it.toDomain() }

    override suspend fun getBusStationByBusNum(id: Int) =
        source
            .getStations(id.toString())
            .mapNotNull { it.toDomain() }

    override suspend fun getBusStationByUserLocation(
        latitude: Double,
        longitude: Double,
        radius: Double
    ) = source
        .getStations(GeoPoint(latitude, longitude), radius)
        .mapNotNull { it.toDomain() }
}