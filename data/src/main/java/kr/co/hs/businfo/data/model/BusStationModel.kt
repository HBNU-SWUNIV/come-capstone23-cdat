package kr.co.hs.businfo.data.model

import com.google.firebase.firestore.GeoPoint

class BusStationModel {
    var bus: List<String>? = null
    var id: String? = null
    var location: GeoPoint? = null
    var name: String? = null
}