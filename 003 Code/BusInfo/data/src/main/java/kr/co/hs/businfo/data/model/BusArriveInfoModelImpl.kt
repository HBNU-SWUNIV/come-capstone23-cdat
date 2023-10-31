package kr.co.hs.businfo.data.model

data class BusArriveInfoModelImpl(
    override val lastBusStopId: Int,
    override val remainBusStopCount: Int,
    override val remainMinute: Int,
    override val busRouteNumber: String,
    override val busRouteId: Int,
    override val destination: String
) : BusArriveInfoModel