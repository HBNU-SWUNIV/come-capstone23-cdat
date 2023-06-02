package kr.co.hs.businfo.data.model

import com.google.firebase.firestore.GeoPoint

interface BusStopInfoModel {
    val nodeId: Int
    val stopId: Int
    val routeId: Int
    val name: String
    val location: GeoPoint
    val sequence: Int
    val routeBusList: List<String>?
}