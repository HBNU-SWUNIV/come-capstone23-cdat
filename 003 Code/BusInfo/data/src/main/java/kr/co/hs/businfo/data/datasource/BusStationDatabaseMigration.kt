package kr.co.hs.businfo.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kr.co.hs.businfo.data.datasource.impl.BusInfoRemoteSourceImpl
import kr.co.hs.businfo.data.model.BusStationModel
import kr.co.hs.businfo.domain.model.BusStation
import org.jetbrains.annotations.TestOnly

object BusStationDatabaseMigration {

    @TestOnly
    suspend fun doMigrate(data: List<BusStation>) {
        val remoteSource = BusInfoRemoteSourceImpl()

        FirebaseFirestore.getInstance().runTransaction {
            data.forEach { busStation -> remoteSource.createStation(it, busStation.toData()) }
        }.await()
    }

    private fun BusStation.toData() = BusStationModel(
        id = stationId.toString(),
        name = stationName,
        latitude = stationLatitude,
        longitude = stationLongitude,
        bus = transitBus
    )
}