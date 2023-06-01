package kr.co.hs.businfo.feature.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kr.co.hs.businfo.R

class DirectionFragment : Fragment() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var destinationEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.direction,
            container,
            false
        )

        destinationEditText = view.findViewById(R.id.edittext_destination)

        val buttonDirection = view.findViewById<Button>(R.id.button_direction)
        buttonDirection.setOnClickListener {
            if (destinationEditText.text.isNotEmpty()) {
                navigateToNaverDirections()
            } else {
                // 대상 위치를 입력하세요.
                Toast.makeText(requireContext(), "대상 위치를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // 위치가 변경될 때마다 자동으로 네이버 지도로 이동하지 않도록 주석 처리
                // val currentLocation = "start=${location.latitude},${location.longitude}"
                // navigateToNaverDirections(currentLocation)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        return view
    }

    private fun navigateToNaverDirections() {
        val destination = destinationEditText.text.toString()

        if (destination.isNotEmpty()) {
            val geoUri = Uri.parse("geo:0,0?q=$destination")
            val intent = Intent(Intent.ACTION_VIEW, geoUri)
            startActivity(intent)
        } else {
            // 대상 위치를 입력하세요.
            Toast.makeText(requireContext(), "대상 위치를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    private fun startLocationUpdates() {
        if (checkLocationPermission()) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )
        } else {
            requestLocationPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListener)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val MIN_TIME_BETWEEN_UPDATES = 1000L
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f
    }
}