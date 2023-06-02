package kr.co.hs.businfo.data.model.daejeonbusrouteinfo

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ServiceResult", strict = false)
data class BusArriveResponse(
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
        @field:Element(name = "BUS_NODE_ID", required = false) var nodeId: Int?,
        @field:Element(name = "BUS_STOP_ID", required = false) var stop: Int?,
        @field:Element(name = "CAR_REG_NO", required = false) var busNumber: String?,
        @field:Element(name = "DESTINATION", required = false) var destination: String?,
        @field:Element(name = "EXTIME_MIN", required = false) var min: Int?,
        @field:Element(name = "EXTIME_SEC", required = false) var sec: Int?,
        @field:Element(name = "INFO_OFFER_TM", required = false) var time: String?,
        @field:Element(name = "LAST_CAT", required = false) var lastCat: String?,
        @field:Element(name = "LAST_STOP_ID", required = false) var lastStopId: Int?,
        @field:Element(name = "MSG_TP", required = false) var messageType: String?,
        @field:Element(name = "ROUTE_CD", required = false) var routeCode: Int?,
        @field:Element(name = "ROUTE_NO", required = false) var routeNumber: String?,
        @field:Element(name = "ROUTE_TP", required = false) var routeType: String?,
        @field:Element(name = "STATUS_POS", required = false) var statusPos: Int?,
        @field:Element(name = "STOP_NAME", required = false) var stopName: String?
    ) {
        constructor() : this(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }
}
