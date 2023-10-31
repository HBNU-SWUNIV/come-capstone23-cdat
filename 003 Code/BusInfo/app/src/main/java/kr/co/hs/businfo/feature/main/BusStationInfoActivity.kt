package kr.co.hs.businfo.feature.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kr.co.hs.businfo.App
import kr.co.hs.businfo.R
import kr.co.hs.businfo.viewmodel.BusStationViewModel

class BusStationInfoActivity : AppCompatActivity() {
    private lateinit var busListAdapter: BusListAdapter
    private lateinit var busStationViewModel: BusStationViewModel
    private lateinit var favoriteFragment: FavoriteFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bus_information)

        val busStationId = intent.getIntExtra("busStationId", -1)
        busStationViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(BusStationViewModel::class.java)

        if (busStationId != -1) {
            lifecycleScope.launch {
                val busStationList = busStationViewModel.getBusStationById(busStationId)
                if (busStationList.isNotEmpty()) {
                    val busStation = busStationList[0]
                    showBusList(busStation.transitBus)

                    val busNameTextView = findViewById<TextView>(R.id.busName)
                    busNameTextView.text = busStation.stationName

                    val favoriteButton = findViewById<Button>(R.id.favorite_button)
                    // FavoriteFragment 동적으로 추가 및 초기화
                    if (!::favoriteFragment.isInitialized) {
                        favoriteFragment = FavoriteFragment()
                        supportFragmentManager.beginTransaction()
                            .add(favoriteFragment, "favoriteFragment")
                            .commit()
                    }
                    favoriteButton.setOnClickListener {
                        val busStopName = busNameTextView.text.toString()
                        val app = applicationContext as App

                        if (!app.favoriteList.contains(busStopName)) {
                            app.favoriteList.add(busStopName)

                            Toast.makeText(this@BusStationInfoActivity, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("BusStationInfoActivity", "버스 정류장 이름: $busStopName")
                        } else {
                            Toast.makeText(this@BusStationInfoActivity, "이미 즐겨찾기에 추가되어 있습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Log.d("BusStationInfoActivity", "No bus station found with ID: $busStationId")
                }
            }
        } else {
            Log.d("BusStationInfoActivity", "busStationId is invalid")
        }
    }

    private fun showBusList(busList: List<String>) {
        val recyclerView = findViewById<RecyclerView>(R.id.rvBusNumbers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        busListAdapter = BusListAdapter(this, busList)
        recyclerView.adapter = busListAdapter

        busListAdapter.setOnItemClickListener(object : BusListAdapter.OnItemClickListener {
            override fun onItemClick(busNumber: String) {
                if (busNumber == "102") {
                    val intent = Intent(this@BusStationInfoActivity, NosunActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }
}
