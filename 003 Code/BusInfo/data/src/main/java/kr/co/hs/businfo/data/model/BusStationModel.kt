package kr.co.hs.businfo.data.model

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.GeoPoint

class BusStationModel {
    var bus: List<String>? = null
    var id: String? = null
    var location: GeoPoint? = null
    var name: String? = null
    var desc: String? = null

    val geoHash by lazy {
        location?.run { GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude)) }
    }

    constructor()

    constructor(
        id: String,
        name: String,
        latitude: Double,
        longitude: Double,
        bus: List<String>
    ) : this() {
        this.id = id
        this.name = name
        this.location = GeoPoint(latitude, longitude)
        this.bus = bus
    }
}