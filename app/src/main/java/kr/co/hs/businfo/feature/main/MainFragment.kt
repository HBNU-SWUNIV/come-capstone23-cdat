package kr.co.hs.businfo.feature.main

import android.Manifest
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
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

class MainFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val RC_PERMISSION_LOCATION = 101
    }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_menuuu, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val searchEditText = view.findViewById<EditText>(R.id.Search)
        val searchButton = view.findViewById<Button>(R.id.search_button)

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

        val settingButton = view.findViewById<ImageButton>(R.id.setting_button)
        settingButton.setOnClickListener {
            showSettingsMenu(settingButton)
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        if (checkLocationPermission()) {
            mapFragment.getMapAsync(this)
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), RC_PERMISSION_LOCATION
            )
        }

        val sharedPref = requireActivity().getSharedPreferences("bus_driver_mode", MODE_PRIVATE)
        val isBusDriverModeEnabled = sharedPref.getBoolean("bus_driver_mode_enabled", false)
        if (isBusDriverModeEnabled) {
            // 버스기사 화면으로 전환
            val intent = Intent(requireContext(), BusDriverActivity::class.java)
            startActivity(intent)
        }

        return view
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

                    val busStationViewModel =
                        ViewModelProvider(this).get(BusStationViewModel::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        // 내 주변 정류소 가져오기
                        val busStations =
                            busStationViewModel.getBusStationByUserLocation(
                                it.latitude,
                                it.longitude,
                                radius
                            )
                        // 가져온 정류소에 대해 마커 추가하기
                        for (busStation in busStations) {
                            val stationLocation =
                                LatLng(busStation.stationLatitude, busStation.stationLongitude)
                            val marker = mMap.addMarker(
                                MarkerOptions().position(stationLocation)
                                    .title(busStation.stationName)
                            )
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

            val busStationViewModel =
                ViewModelProvider(this).get(BusStationViewModel::class.java)
            CoroutineScope(Dispatchers.Main).launch {
                // 지도 중심 위치 주변 정류소 가져오기
                val busStations =
                    busStationViewModel.getBusStationByUserLocation(
                        center.latitude,
                        center.longitude,
                        radius
                    )
                // 기존 마커 제거
                mMap.clear()
                // 가져온 정류소에 대해 마커 추가하기
                for (busStation in busStations) {
                    val stationLocation =
                        LatLng(busStation.stationLatitude, busStation.stationLongitude)
                    val marker = mMap.addMarker(
                        MarkerOptions().position(stationLocation)
                            .title(busStation.stationName)
                    )
                    marker?.tag = busStation // 마커에 정류소 정보를 태그로 저장
                }
            }
        }

        mMap.setOnMarkerClickListener { marker ->
            val busStation = marker.tag as? BusStation
            if (busStation != null) {
                // 마커 클릭 시 bus_information 화면으로 이동
                val intent = Intent(requireContext(), BusStationInfoActivity::class.java)
                intent.putExtra("busStationId", busStation.stationId) // 선택한 정류소의 ID를 전달
                startActivity(intent)
            }
            true
        }
    }

    private fun performSearch(query: String, searchEditText: EditText) {
        val busStationViewModel =
            ViewModelProvider(this).get(BusStationViewModel::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            val busStations = busStationViewModel.getBusStationByName(query)
            if (busStations.isNotEmpty()) {
                updateMapWithSearchResults(busStations)
            } else {
                Toast.makeText(
                    requireContext(),
                    "검색 결과가 없습니다. 다시 검색해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                searchEditText.text.clear() // 검색 창 초기화
            }
        }
    }

    private fun updateMapWithSearchResults(busStations: List<BusStation>) {
        mMap.clear() // 기존 마커 제거

        if (busStations.isNotEmpty()) {
            for (busStation in busStations) {
                val stationLocation =
                    LatLng(busStation.stationLatitude, busStation.stationLongitude)
                val marker = mMap.addMarker(
                    MarkerOptions().position(stationLocation)
                        .title(busStation.stationName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )
                marker?.tag = busStation
            }
            val stationLocation =
                LatLng(busStations[0].stationLatitude, busStations[0].stationLongitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stationLocation, 15f))
        }
    }

    private fun showSettingsMenu(anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            // 설정 메뉴 옵션을 선택했을 때 수행할 동작 작성
            when (menuItem.itemId) {
                R.id.option1 -> {
                    val intent = Intent(requireContext(), BusDriverSetting::class.java)
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
        if (requestCode == RC_PERMISSION_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}