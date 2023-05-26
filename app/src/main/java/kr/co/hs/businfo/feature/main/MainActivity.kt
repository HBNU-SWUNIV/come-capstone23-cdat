package kr.co.hs.businfo.feature.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.hs.businfo.R
import kr.co.hs.businfo.domain.model.BusStation
import kr.co.hs.businfo.feature.BusDriver.BusDriverActivity
import kr.co.hs.businfo.viewmodel.BusStationViewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val searchEditText = findViewById<EditText>(R.id.Search)
        val searchButton = findViewById<Button>(R.id.search_button) // 검색 버튼 추가

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            performSearch(query, searchEditText)
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString()
                performSearch(query, searchEditText)
                true
            } else {
                false
            }
        }
        val setting_button = findViewById<ImageButton>(R.id.setting_button)

        setting_button.setOnClickListener {
            // 클릭 이벤트 발생 시 설정 메뉴를 표시하는 코드 작성
            showSettingsMenu(setting_button)
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true

        val radius = 500.0

        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLocation = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

                    val busStationViewModel = ViewModelProvider(this).get(BusStationViewModel::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        // 내 주변 정류소 가져오기
                        val busStations = busStationViewModel.getBusStationByUserLocation(it.latitude, it.longitude, radius)
                        // 가져온 정류소에 대해 마커 추가하기
                        for (busStation in busStations) {
                            val stationLocation = LatLng(busStation.stationLatitude, busStation.stationLongitude)
                            val marker = mMap.addMarker(MarkerOptions().position(stationLocation).title(busStation.stationName))
                            marker?.tag = busStation // 마커에 정류소 정보를 태그로 저장
                        }
                    }
                }
            }
        }

        mMap.setOnCameraMoveListener {
            val visibleRegion = mMap.projection.visibleRegion
            val bounds = LatLngBounds.Builder()
            bounds.include(visibleRegion.farLeft)
            bounds.include(visibleRegion.farRight)
            bounds.include(visibleRegion.nearLeft)
            bounds.include(visibleRegion.nearRight)
            val center = bounds.build().center

            val busStationViewModel = ViewModelProvider(this).get(BusStationViewModel::class.java)
            CoroutineScope(Dispatchers.Main).launch {
                // 지도 중심 위치 주변 정류소 가져오기
                val busStations = busStationViewModel.getBusStationByUserLocation(center.latitude, center.longitude, radius)
                // 기존 마커 제거
                mMap.clear()
                // 가져온 정류소에 대해 마커 추가하기
                for (busStation in busStations) {
                    val stationLocation = LatLng(busStation.stationLatitude, busStation.stationLongitude)
                    val marker = mMap.addMarker(MarkerOptions().position(stationLocation).title(busStation.stationName))
                    marker?.tag = busStation // 마커에 정류소 정보를 태그로 저장
                }
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

    private fun performSearch(query: String, searchEditText: EditText) {
        val busStationViewModel = ViewModelProvider(this).get(BusStationViewModel::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            val busStations = busStationViewModel.getBusStationByName(query)
            if (busStations.isNotEmpty()) {
                updateMapWithSearchResults(busStations)
            } else {
                Toast.makeText(this@MainActivity, "검색 결과가 없습니다. 다시 검색해주세요.", Toast.LENGTH_SHORT).show()
                searchEditText.text.clear() // 검색 창 초기화
            }
        }
    }

    private fun updateMapWithSearchResults(busStations: List<BusStation>) {
        mMap.clear() // 기존 마커 제거

        if (busStations.isNotEmpty()) {
            for (busStation in busStations) {
                val stationLocation = LatLng(busStation.stationLatitude, busStation.stationLongitude)
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(stationLocation)
                        .title(busStation.stationName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )
                marker?.tag = busStation
            }
            val stationLocation = LatLng(busStations[0].stationLatitude, busStations[0].stationLongitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stationLocation, 15f))
        }
    }
    private fun showSettingsMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            // 설정 메뉴 옵션을 선택했을 때 수행할 동작 작성
            when (menuItem.itemId) {
                R.id.option1 -> {
                    val intent = Intent(this, BusDriverSetting::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == BusDriverActivity.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap)
            }
        }
    }
    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                BusDriverActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }
}