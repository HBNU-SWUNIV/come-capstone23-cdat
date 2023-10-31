package kr.co.hs.businfo.data.model

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.GeoPoint

data class BusRouteModel(
    override val nodeId: Int,
    override val stopId: Int,
    override val routeId: Int,
    override val name: String,
    override val location: GeoPoint,
    override val sequence: Int,
    override var routeBusList: List<String> = emptyList()
) : BusStopInfoModel, Comparable<BusRouteModel> {

    val geoHash by lazy {
        location.run { GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude)) }
    }

    override fun compareTo(other: BusRouteModel): Int {
        return sequence.compareTo(other.sequence)
    }

    constructor() : this(0, 0, 0, "", GeoPoint(0.0, 0.0), 0)

    constructor(
        nodeId: Int,
        stopId: Int,
        routeId: Int,
        name: String,
        latitude: Double,
        longitude: Double,
        sequence: Int
    ) : this(nodeId, stopId, routeId, name, GeoPoint(latitude, longitude), sequence)
}
