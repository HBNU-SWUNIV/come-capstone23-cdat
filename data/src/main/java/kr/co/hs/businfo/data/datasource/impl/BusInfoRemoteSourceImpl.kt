package kr.co.hs.businfo.data.datasource.impl

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kr.co.hs.businfo.data.datasource.BusInfoRemoteSource
import kr.co.hs.businfo.data.model.BusStationModel

class BusInfoRemoteSourceImpl : BusInfoRemoteSource {
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
}