package kr.co.hs.businfo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kr.co.hs.businfo.data.datasource.BusStationDatabaseMigration
import kr.co.hs.businfo.data.repository.BusStationRepositoryImpl
import kr.co.hs.businfo.domain.model.BusStation
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BusInfoDataTest {

    val busStations = listOf(
        BusStation(
            "수통골기점", 10000, 36.345996, 127.295215,
            listOf("102", "103", "104")
        ),
        BusStation(
            "부대통령", 10001, 36.348643, 127.297723,
            listOf("113", "117")
        ),
        BusStation(
            "덕명중학교", 10002, 36.346325, 127.301177,
            listOf("113", "117")
        ),
        BusStation(
            "수통골입구", 10003, 36.348185, 127.296162,
            listOf("102", "103", "104")
        ),
        BusStation(
            "한밭대정문", 10004, 36.351048, 127.29764,
            listOf("102", "103", "104", "113", "117")
        ),
        BusStation(
            "삼성화재연수원", 10005, 36.354308, 127.300999,
            listOf("102", "103", "104", "113", "117")
        ),
        BusStation(
            "하우스스토리", 10006, 36.358296, 127.304091,
            listOf("102", "103", "104", "113", "117")
        ),
        BusStation(
            "덕명네거리", 10007, 36.36016, 127.306493,
            listOf("102", "103", "104", "113")
        ),
        BusStation(
            "수정초등학교", 10008, 36.366647, 127.313864,
            listOf("117")
        ),
        BusStation(
            "주유소", 10009, 36.359117, 127.309293,
            listOf("102", "103", "104", "113", "107")
        ),
        BusStation(
            "리빙포레아파트", 10010, 36.343535, 127.303329,
            listOf("113", "117")
        ),
    )

    @Before
    fun initStation() = runBlocking {
        BusStationDatabaseMigration.doMigrate(busStations)
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
        val repository = BusStationRepositoryImpl()

        val centerLatitude = 36.351048
        val centerLongitude = 127.29764
        val radiusInMeters = 500.0

        val list = repository
            .getBusStationByUserLocation(centerLatitude, centerLongitude, radiusInMeters)

        list.forEach {
            val centerLocation = GeoLocation(centerLatitude, centerLongitude)
            val distance =
                GeoLocation(it.stationLatitude, it.stationLongitude)
                    .run { GeoFireUtils.getDistanceBetween(centerLocation, this) }
            assertTrue(distance <= (radiusInMeters))
        }

    }
}