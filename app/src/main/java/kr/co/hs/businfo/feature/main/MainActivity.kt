package kr.co.hs.businfo.feature.main

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.hs.businfo.R
import kr.co.hs.businfo.domain.model.BusStation
import kr.co.hs.businfo.viewmodel.BusStationViewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchEditText = findViewById<EditText>(R.id.Search)
        val searchButton = findViewById<Button>(R.id.search_button) // 검색 버튼 추가

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            performSearch(query)
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString()
                performSearch(query)
                true
            } else {
                false
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val hanbat = LatLng(36.351080, 127.297754)
        val radius = 500.0

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanbat, 15.5f))
        val busStationViewModel = ViewModelProvider(this).get(BusStationViewModel::class.java)
        // CoroutineScope를 사용하여 비동기적으로 데이터 가져오기
        CoroutineScope(Dispatchers.Main).launch {
            // 내 주변 정류소 가져오기
            val busStations = busStationViewModel.getBusStationByUserLocation(hanbat.latitude, hanbat.longitude, radius)
            // 가져온 정류소에 대해 마커 추가하기
            for (busStation in busStations) {
                val stationLocation = LatLng(busStation.stationLatitude, busStation.stationLongitude)
                val marker = mMap.addMarker(MarkerOptions().position(stationLocation).title(busStation.stationName))
                marker?.tag = busStation // 마커에 정류소 정보를 태그로 저장
            }
        }

        mMap.setOnMarkerClickListener { marker ->
            val busStation = marker.tag as? BusStation
            if (busStation != null) {
                // 마커 클릭 시 bus_information 화면으로 이동
                val intent = Intent(this@MainActivity, BusStationInfoActivity::class.java)
                intent.putExtra("busStationId", busStation.stationId) // 선택한 정류소의 ID를 전달
                startActivity(intent)
            }
            true
        }
    }

    private fun performSearch(query: String) {
        val busStationViewModel = ViewModelProvider(this).get(BusStationViewModel::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            val busStations = busStationViewModel.getBusStationByName(query)
            updateMapWithSearchResults(busStations)
        }
    }

    private fun updateMapWithSearchResults(busStations: List<BusStation>) {
        mMap.clear() // 기존 마커 제거

        if (busStations.isNotEmpty()) {
            val firstBusStation = busStations[0] // 첫 번째 검색 결과를 사용하여 마커와 카메라 위치 설정

            val stationLocation = LatLng(firstBusStation.stationLatitude, firstBusStation.stationLongitude)
            val marker = mMap.addMarker(MarkerOptions().position(stationLocation).title(firstBusStation.stationName))
            marker?.tag = firstBusStation

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stationLocation, 15.5f))
        }
    }
}