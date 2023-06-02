package kr.co.hs.businfo.data.model.daejeonbusrouteinfo

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ServiceResult", strict = false)
data class BusRouteResponse(
    @field:Element(name = "msgHeader")
    var header: Header?,
    @field:Element(name = "msgBody")
    var body: Body?
) {
    constructor() : this(null, null)

    @Root(name = "msgBody", strict = false)
    data class Body(
        @field:ElementList(name = "itemList", inline = true)
        var list: List<Item>?
    ) {
        constructor() : this(null)
    }

    @Root(name = "itemList", strict = false)
    data class Item(
        @field:Element(name = "BUSSTOP_ENG_NM", required = false) var nameEng: String?,
        @field:Element(name = "BUSSTOP_NM", required = false) var name: String?,
        @field:Element(name = "BUSSTOP_SEQ", required = false) var seq: Int?,
        @field:Element(name = "BUSSTOP_TP", required = false) var type: String?,
        @field:Element(name = "BUS_NODE_ID", required = false) var nodeId: Int?,
        @field:Element(name = "BUS_STOP_ID", required = false) var stopId: Int?,
        @field:Element(name = "GPS_LATI", required = false) var latitude: Double?,
        @field:Element(name = "GPS_LONG", required = false) var longitude: Double?,
        @field:Element(name = "ROAD_NM", required = false) var roadName: String?,
        @field:Element(name = "ROAD_NM_ADDR", required = false) var roadAddress: String?,
        @field:Element(name = "ROUTE_CD", required = false) var routeId: Int?,
        @field:Element(name = "TOTAL_DIST", required = false) var dist: Int?
    ) {
        constructor() : this(null, null, null, null, null, null, null, null, null, null, null, null)
    }
}