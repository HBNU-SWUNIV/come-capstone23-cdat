package kr.co.hs.businfo.data.datasource.impl

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kr.co.hs.businfo.data.datasource.DaejeonBusInfoProvider
import kr.co.hs.businfo.data.datasource.DaejeonBusRemoteSource
import kr.co.hs.businfo.data.mapper.DaejeonBusMapper.toData
import kr.co.hs.businfo.data.model.BusArriveInfoModel
import kr.co.hs.businfo.data.model.BusRouteModel
import kr.co.hs.businfo.data.model.BusStopInfoModel
import java.util.TreeSet

class DaejeonBusRemoteSourceImpl(private val key: String) : DaejeonBusRemoteSource {

    private val busRouteCollection = FirebaseFirestore.getInstance()
        .collection("daejeon_bus")
        .document("v1")
        .collection("route")

    override fun createStation(transaction: Transaction, model: BusRouteModel) {
        transaction.set(busRouteCollection.document(model.stopId.toString()), model)
    }

    fun createStation(model: BusRouteModel) {
        busRouteCollection.document(model.stopId.toString()).set(model, SetOptions.merge())
    }

    override suspend fun getStations(busNumber: String): List<BusStopInfoModel> {
        val result = busRouteCollection
            .whereArrayContains("routeBusList", busNumber)
            .get()
            .await()
        return result.documents
            .mapNotNull { it.toObject(BusRouteModel::class.java) }
    }

    override suspend fun getStations(
        geoPoint: GeoPoint,
        radiusInMeters: Double
    ): List<BusStopInfoModel> {
        val center = GeoLocation(geoPoint.latitude, geoPoint.longitude)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeters)

        val result = coroutineScope {
            val jobs = bounds.map {
                async {
                    busRouteCollection
                        .orderBy("geoHash")
                        .startAt(it.startHash)
                        .endAt(it.endHash)
                        .get()
                        .await()
                }
            }
            awaitAll(*jobs.toTypedArray())
        }.mapNotNull { snapshot ->
            snapshot
                .documents
                .mapNotNull { it.toObject(BusRouteModel::class.java) }
                .mapNotNull {
                    it.location
                        ?.run { GeoLocation(latitude, longitude) }
                        ?.run { GeoFireUtils.getDistanceBetween(this, center) }
                        .takeIf { distanceInM -> distanceInM != null && distanceInM <= radiusInMeters }
                        ?.run { it }
                }
        }.flatten()

        return result
    }

    override suspend fun findStationByName(name: String): List<BusStopInfoModel> {
        val result = busRouteCollection
            .orderBy("name")
            .startAt(name)
            .endAt(name + "\uf8ff")
            .get()
            .await()
        return result.documents
            .mapNotNull { it.toObject(BusRouteModel::class.java) }
    }

    override suspend fun getStation(stopId: Int): BusStopInfoModel? {
        return busRouteCollection
            .document(stopId.toString())
            .get()
            .await()
            .toObject(BusRouteModel::class.java)
    }

    override suspend fun getRoute(routeId: Int): Set<BusStopInfoModel> {
        val set = TreeSet<BusRouteModel>()
        busRouteCollection
            .whereEqualTo("routeId", routeId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(BusRouteModel::class.java) }
            .run { set.addAll(this) }
        return set
    }

    override suspend fun getArriveInfo(stopId: Int): List<BusArriveInfoModel> {
        val request = DaejeonBusInfoProvider.getRequest()
        return request.getArriveInfoByStopId(key, stopId).body()?.body?.list?.map { it.toData() }
            ?: emptyList()
    }
}