package kr.co.hs.businfo.data.datasource

import kr.co.hs.businfo.data.model.daejeonbusrouteinfo.BusArriveResponse
import kr.co.hs.businfo.data.model.daejeonbusrouteinfo.BusRouteResponse
import kr.co.hs.businfo.data.model.daejeonbusrouteinfo.BusStopResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DaejeonBusInfoProvider {

    companion object {
        private fun retrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("http://openapitraffic.daejeon.go.kr/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
        }

        fun getRequest(): DaejeonBusInfoProvider {
            return retrofit().create(DaejeonBusInfoProvider::class.java)
        }
    }

    @GET("/api/rest/busRouteInfo/getStaionByRouteAll")
    suspend fun getRouteAll(
        @Query("serviceKey") serviceKey: String,
        @Query("reqPage") reqPage: Int
    ): Response<BusRouteResponse>

    @GET("/api/rest/busRouteInfo/getStaionByRoute")
    suspend fun getRoute(
        @Query("serviceKey") serviceKey: String,
        @Query("busRouteId") routeId: Int
    ): Response<BusRouteResponse>

    @GET("/api/rest/stationinfo/getStationByUid")
    suspend fun getBusStopByStopId(
        @Query("serviceKey") serviceKey: String,
        @Query("arsId") stopId: Int
    ): Response<BusStopResponse>

    @GET("/api/rest/stationinfo/getStationByStopID")
    suspend fun getBusStopByNodeId(
        @Query("serviceKey") serviceKey: String,
        @Query("BusStopID") nodeId: Int
    ): Response<BusStopResponse>

    @GET("/api/rest/arrive/getArrInfoByUid")
    suspend fun getArriveInfoByStopId(
        @Query("serviceKey") serviceKey: String,
        @Query("arsId") stopId: Int
    ): Response<BusArriveResponse>

    @GET("/api/rest/arrive/getArrInfoByStopID")
    suspend fun getArriveInfoByNodeId(
        @Query("serviceKey") serviceKey: String,
        @Query("BusStopID") nodeId: Int
    ): Response<BusArriveResponse>
}