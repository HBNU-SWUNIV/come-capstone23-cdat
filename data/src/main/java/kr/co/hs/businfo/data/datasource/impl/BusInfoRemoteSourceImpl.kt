package kr.co.hs.businfo.data.datasource.impl

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kr.co.hs.businfo.data.datasource.BusInfoRemoteSource
import kr.co.hs.businfo.data.model.BusStationModel

internal class BusInfoRemoteSourceImpl : BusInfoRemoteSource {
    override fun createStation(transaction: Transaction, busStation: BusStationModel) {
        transaction.set(busStationCollection.document(busStation.id!!), busStation)
    }

    override suspend fun getStations(busNumber: String): List<BusStationModel> {
        val result = busStationCollection
            .whereArrayContains("bus", busNumber)
            .get()
            .await()
        return result.documents
            .mapNotNull { it.toObject(BusStationModel::class.java) }
    }

    private val busStationCollection = FirebaseFirestore.getInstance()
        .collection("busStation")

    override suspend fun findStationByName(name: String): List<BusStationModel> {
        val result = busStationCollection
            .orderBy("name")
            .startAt(name)
            .endAt(name + "\uf8ff")
            .get()
            .await()
        return result.documents
            .mapNotNull { it.toObject(BusStationModel::class.java) }
    }

    override suspend fun getStation(id: String) = busStationCollection
        .document(id)
        .get()
        .await()
        .toObject(BusStationModel::class.java)

    override suspend fun getStations(
        geoPoint: GeoPoint,
        radiusInMeters: Double
    ): List<BusStationModel> {
        val center = GeoLocation(geoPoint.latitude, geoPoint.longitude)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeters)

        val result = coroutineScope {
            val jobs = bounds.map {
                async {
                    busStationCollection
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
                .mapNotNull { it.toObject(BusStationModel::class.java) }
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
}