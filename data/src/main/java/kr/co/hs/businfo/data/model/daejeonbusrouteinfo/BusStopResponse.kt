package kr.co.hs.businfo.data.model.daejeonbusrouteinfo

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "ServiceResult", strict = false)
data class BusStopResponse(
    @field:Element(name = "msgHeader")
    var header: Header?,
    @field:Element(name = "msgBody")
    var body: Body?
) {
    constructor() : this(null, null)

    @Root(name = "msgBody", strict = false)
    data class Body(
        @field:Element(name = "itemList", required = false)
        var item: Item?
    ) {
        constructor() : this(null)
    }

    @Root(name = "itemList", strict = false)
    data class Item(
        @field:Element(name = "ARO_BUSSTOP_ID", required = false) var busStopId: Int?,
        @field:Element(name = "BUSSTOP_ENG_NM", required = false) var engName: String?,
        @field:Element(name = "BUSSTOP_NM", required = false) var name: String?,
        @field:Element(name = "BUS_NODE_ID", required = false) var nodeId: Int?,
        @field:Element(name = "GPS_LATI", required = false) var latitude: Double?,
        @field:Element(name = "GPS_LONG", required = false) var longitude: Double?,
        @field:Element(name = "ROAD_NM", required = false) var roadName: String?,
        @field:Element(name = "ROAD_NM_ADDR", required = false) var roadNameAddress: String?,
        @field:Element(name = "ROUTE_NO", required = false) var routeNumbers: String?
    ) {
        constructor() : this(null, null, null, null, null, null, null, null, null)
    }
}