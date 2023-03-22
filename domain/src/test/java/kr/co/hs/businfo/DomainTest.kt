package kr.co.hs.businfo

import kr.co.hs.businfo.domain.BusInfoDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class DomainTest {
    @Test
    fun testDomain() {
        assertEquals(3, BusInfoDomain().get())
    }
}