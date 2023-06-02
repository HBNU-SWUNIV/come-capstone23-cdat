package kr.co.hs.businfo.data.mapper

import kr.co.hs.businfo.data.model.BusArriveInfoModel
import kr.co.hs.businfo.data.model.BusArriveInfoModelImpl
import kr.co.hs.businfo.data.model.daejeonbusrouteinfo.BusArriveResponse

object DaejeonBusMapper {

    fun BusArriveResponse.Item.toData(): BusArriveInfoModel {
        return BusArriveInfoModelImpl(
            lastBusStopId = lastStopId ?: -1,
            remainBusStopCount = statusPos ?: -1,
            remainMinute = min ?: -1,
            busRouteNumber = routeNumber ?: "",
            busRouteId = routeCode ?: -1,
            destination = destination ?: ""
        )
    }
}