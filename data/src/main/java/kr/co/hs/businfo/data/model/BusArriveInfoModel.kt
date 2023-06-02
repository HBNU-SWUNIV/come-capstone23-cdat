package kr.co.hs.businfo.data.model

interface BusArriveInfoModel {
    val lastBusStopId: Int
    val remainBusStopCount: Int
    val remainMinute: Int
    val busRouteNumber: String
    val busRouteId: Int
    val destination: String
}