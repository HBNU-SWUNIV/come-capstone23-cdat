package kr.co.hs.businfo.domain.model

data class BusStation(
    val stationName: String,
    val stationId: Int,
    val stationLatitude: Double,
    val stationLongitude: Double,
    val transitBus: List<String>
)