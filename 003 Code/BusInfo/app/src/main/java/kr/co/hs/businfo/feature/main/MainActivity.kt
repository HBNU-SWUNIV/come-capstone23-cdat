package kr.co.hs.businfo.feature.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kr.co.hs.businfo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.co.hs.businfo.App
import kr.co.hs.businfo.viewmodel.BusStationViewModel

class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var busStationViewModel: BusStationViewModel
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        busStationViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(BusStationViewModel::class.java)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.menu_frame_layout, MainFragment())
                .commit()
        }

        requestLocationPermission()
    }

    override fun onResume() {
        val app = applicationContext as App
        busStationViewModel.favoriteList = app.favoriteList
        super.onResume()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val selectedFragment: Fragment = when (item.itemId) {
            R.id.favorite -> FavoriteFragment()
            R.id.home -> MainFragment()
            R.id.direction -> DirectionFragment()
            R.id.trip -> TripFragment()
            else -> MainFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.menu_frame_layout, selectedFragment)
            .commit()

        return true
    }

    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }
}