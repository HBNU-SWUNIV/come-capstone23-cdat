package kr.co.hs.businfo.data.mapper

import kr.co.hs.businfo.data.model.BusStationModel
import kr.co.hs.businfo.data.model.BusStopInfoModel
import kr.co.hs.businfo.domain.model.BusStation

object BusStationMapper {
    fun BusStationModel.toDomain(): BusStation? {
        val id = id?.toIntOrNull() ?: return null
        val latitude = location?.latitude ?: return null
        val longitude = location?.longitude ?: return null
        return BusStation(
            stationId = id,
            stationName = name ?: "unknown",
            stationLatitude = latitude,
            stationLongitude = longitude,
            transitBus = bus ?: emptyList()
        )
    }

    fun BusStopInfoModel.toDomain(): BusStation? {
        return BusStation(
            stationId = stopId,
            stationName = name,
            stationLatitude = location.latitude,
            stationLongitude = location.longitude,
            transitBus = routeBusList ?: emptyList()
        )
    }


}