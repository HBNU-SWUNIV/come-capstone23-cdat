package kr.co.hs.businfo

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kr.co.hs.businfo.data.repository.BusStationRepositoryImpl
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BusInfoDataTest {

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
}