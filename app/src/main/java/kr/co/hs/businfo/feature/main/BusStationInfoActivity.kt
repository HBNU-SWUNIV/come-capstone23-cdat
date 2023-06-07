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
import kr.co.hs.businfo.R
import kr.co.hs.businfo.viewmodel.BusStationViewModel

class BusStationInfoActivity : AppCompatActivity() {
    private lateinit var busListAdapter: BusListAdapter
    private lateinit var busStationViewModel: BusStationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bus_information)

        val busStationId = intent.getIntExtra("busStationId", -1)
        busStationViewModel = ViewModelProvider(this).get(BusStationViewModel::class.java)

        if (busStationId != -1) {
            // 정류소 ID를 사용하여 정류소 정보 가져오기
            lifecycleScope.launch {
                val busStationList = busStationViewModel.getBusStationById(busStationId)
                if (busStationList.isNotEmpty()) {
                    val busStation = busStationList[0]
                    showBusList(busStation.transitBus) // transitBus 정보 사용

                    // 정류소 이름 설정
                    val busNameTextView = findViewById<TextView>(R.id.busName)
                    busNameTextView.text = busStation.stationName

                    val favoriteButton = findViewById<Button>(R.id.favorite_button)

                    favoriteButton.setOnClickListener {
                        val busStopName = busNameTextView.text.toString()

                        val favoriteFragment = supportFragmentManager.findFragmentById(R.id.favorite) as? FavoriteFragment
                        favoriteFragment?.addFavoriteItem(busStopName)
                        Toast.makeText(this@BusStationInfoActivity, "즐겨찾기 추가되었습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("BusStationInfoActivity", "버스 정류장 이름: $busStopName") // 추가된 로그
                    }

                } else {
                    Log.d("BusStationInfoActivity", "No bus station found with ID: $busStationId")
                    // 해당 ID에 해당하는 정류소를 찾을 수 없는 경우 처리
                }
            }
        } else {
            Log.d("BusStationInfoActivity", "busStationId is invalid")
            // busStationId가 유효하지 않은 경우 처리
        }
    }

    private fun showBusList(busList: List<String>) {
        // RecyclerView를 찾아서 어댑터를 설정하여 표시
        val recyclerView = findViewById<RecyclerView>(R.id.rvBusNumbers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        busListAdapter = BusListAdapter(this, busList)
        recyclerView.adapter = busListAdapter

        // 리스트 아이템 클릭 이벤트 처리
        busListAdapter.setOnItemClickListener(object : BusListAdapter.OnItemClickListener {
            override fun onItemClick(busNumber: String) {
                if (busNumber == "102") {
                    // 102번을 눌렀을 때 NosunActivity로 이동
                    val intent = Intent(this@BusStationInfoActivity, NosunActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }
}