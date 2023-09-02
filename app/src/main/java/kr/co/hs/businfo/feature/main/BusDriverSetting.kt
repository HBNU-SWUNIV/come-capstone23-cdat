package kr.co.hs.businfo.feature.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kr.co.hs.businfo.R
import kr.co.hs.businfo.feature.BusDriver.BusDriverActivity
import kr.co.hs.businfo.feature.BusDriver.BusDriverAuthentication
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.*
import java.util.*

class BusDriverSetting : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var switchButton: Switch

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val locationUpdateInterval = 5000L // 5 seconds

    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.busdriver_setting)

        val driverAuthenticationTextView = findViewById<TextView>(R.id.driver_authentication)
        driverAuthenticationTextView.setOnClickListener {
            val intent = Intent(this, BusDriverAuthentication::class.java)
            startActivity(intent)
        }
        sharedPref = getSharedPreferences("bus_driver_mode", MODE_PRIVATE)
        switchButton = findViewById(R.id.switchButton)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = locationUpdateInterval
            fastestInterval = locationUpdateInterval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocationToFirestore(location)
                }
            }
        }

        val isBusDriverModeEnabled = sharedPref.getBoolean("bus_driver_mode_enabled", false)
        switchButton.isChecked = isBusDriverModeEnabled

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableBusDriverMode()
                startLocationUpdates()
                startLocationUpdateTimer() // 5초마다 위치 업데이트 시작
            } else {
                disableBusDriverMode()
                stopLocationUpdates()
                stopLocationUpdateTimer() // 위치 업데이트 타이머 종료
            }
        }
    }

    private fun enableBusDriverMode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val db = Firebase.firestore
                    with(sharedPref.edit()) {
                        putBoolean("bus_driver_mode_enabled", true)
                        apply()
                    }

                    updateLocationToFirestore(location)

                    Toast.makeText(this, "버스 기사 모드로 전환되었습니다.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, BusDriverActivity::class.java)
                    startActivity(intent)

                    finish()
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), YOUR_REQUEST_CODE)
        }
    }

    private fun disableBusDriverMode() {
        with(sharedPref.edit()) {
            putBoolean("bus_driver_mode_enabled", false)
            apply()
        }

        Toast.makeText(this, "기본 화면으로 전환되었습니다.", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), YOUR_REQUEST_CODE)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationUpdateTimer() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // 위치 업데이트를 수행하는 코드
                if (ContextCompat.checkSelfPermission(this@BusDriverSetting, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            updateLocationToFirestore(location)
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(this@BusDriverSetting, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), YOUR_REQUEST_CODE)
                }
            }
        }, 0, locationUpdateInterval)
    }

    private fun stopLocationUpdateTimer() {
        timer.cancel()
    }

    private fun updateLocationToFirestore(location: Location) {
        val db = Firebase.firestore

        val currentTime = System.currentTimeMillis()

        val user = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to currentTime
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                // Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                // Log.w(TAG, "Error adding document", e)
            }
    }

    companion object {
        private const val TAG = "BusDriverSetting"
        private const val YOUR_REQUEST_CODE = 123
    }
}