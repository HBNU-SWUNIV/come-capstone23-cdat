package kr.co.hs.businfo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kr.co.hs.businfo.data.datasource.impl.BusInfoRemoteSourceImpl
import kr.co.hs.businfo.data.model.BusStationModel
import kr.co.hs.businfo.data.repository.BusStationRepositoryImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BusInfoDataTest {

    val data = listOf(
        BusStationModel(
            "10000", "수통골기점", 36.345996, 127.295215,
            listOf("102", "103", "104")
        ),
        BusStationModel(
            "10001", "부대통령", 36.348643, 127.297723,
            listOf("113", "117")
        ).apply {
            desc = "부대통령 앞"
        },
        BusStationModel(
            "10002", "덕명중학교", 36.346325, 127.301177,
            listOf("113", "117")
        ).apply {
            desc = "덕명중 앞"
        },
        BusStationModel(
            "10003", "수통골입구", 36.348185, 127.296162,
            listOf("102", "103", "104")
        ).apply {
            desc = "황서옥 앞"
        },
        BusStationModel(
            "10004", "한밭대정문", 36.351048, 127.29764,
            listOf("102", "103", "104", "113", "117")
        ).apply {
            desc = "한밭대 정문"
        },
        BusStationModel(
            "10005", "삼성화재연수원", 36.354308, 127.300999,
            listOf("102", "103", "104", "113", "117")
        ),
        BusStationModel(
            "10006", "하우스스토리", 36.358296, 127.304091,
            listOf("102", "103", "104", "113", "117")
        ),
        BusStationModel(
            "10007", "덕명네거리", 36.36016, 127.306493,
            listOf("102", "103", "104", "113")
        ),
        BusStationModel(
            "10008", "수정초등학교", 36.366647, 127.313864,
            listOf("117")
        ),
        BusStationModel(
            "10009", "주유소", 36.359117, 127.309293,
            listOf("102", "103", "104", "113", "107")
        ),
        BusStationModel(
            "10010", "리빙포레아파트", 36.343535, 127.303329,
            listOf("113", "117")
        )
    )

    @Before
    fun initStation() = runBlocking {
        val remoteSource = BusInfoRemoteSourceImpl()

        FirebaseFirestore.getInstance().runTransaction {
            data.forEach { busStation -> remoteSource.createStation(it, busStation) }
        }

        return@runBlocking
    }

    @Test
    fun getBusStationTest() = runBlocking {
        val busStationRepository = BusStationRepositoryImpl()

        // 113번 버스 노선 요청
        val result = busStationRepository.getBusStationByBusNum(113)

        assertEquals(result.size, 8)

        return@runBlocking
    }

    @Test
    fun findBusStation() = runBlocking {
        val repository = BusStationRepositoryImpl()
        val result = repository.searchBusStationByName("부대통")
        assertEquals(result.size, 1)
        return@runBlocking
    }

    @Test
    fun getBusStationAroundTest() = runBlocking {
        val busRemoteSource = BusInfoRemoteSourceImpl()

        val center = GeoPoint(36.351048, 127.29764)
        val distanceKM = 1.0

        val list = busRemoteSource.getStations(center, distanceKM)

        list.forEach {
            val centerLocation = GeoLocation(center.latitude, center.longitude)
            val distance =
                it.location
                    ?.run { GeoLocation(latitude, longitude) }
                    ?.run { GeoFireUtils.getDistanceBetween(centerLocation, this) }
                    ?: 0.0
            assertTrue(distance <= (distanceKM * 1000))
        }

    }
}