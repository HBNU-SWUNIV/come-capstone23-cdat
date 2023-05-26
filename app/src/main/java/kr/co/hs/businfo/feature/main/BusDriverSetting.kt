package kr.co.hs.businfo.feature.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.co.hs.businfo.R
import kr.co.hs.businfo.feature.BusDriver.BusDriverActivity
import kr.co.hs.businfo.feature.BusDriver.BusDriverAuthentication

class BusDriverSetting : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var switchButton: Switch

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

        // 저장된 설정 값을 불러와 스위치 버튼에 반영
        val isBusDriverModeEnabled = sharedPref.getBoolean("bus_driver_mode_enabled", false)
        switchButton.isChecked = isBusDriverModeEnabled

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 버튼이 On 상태일 때 동작 수행
                enableBusDriverMode()
            } else {
                // 버튼이 Off 상태일 때 동작 수행
                disableBusDriverMode()
            }
        }
    }
    private fun enableBusDriverMode() {
        // 버스 기사 모드 설정 값을 저장
        with(sharedPref.edit()) {
            putBoolean("bus_driver_mode_enabled", true)
            apply()
        }

        // 화면에 메시지 표시
        Toast.makeText(this, "버스 기사 모드로 전환되었습니다.", Toast.LENGTH_SHORT).show()

        // 버스기사 화면으로 전환
        val intent = Intent(this, BusDriverActivity::class.java)
        startActivity(intent)

        // 현재 액티비티 종료 (선택 사항)
        finish()
    }

    private fun disableBusDriverMode() {
        // 버스 기사 모드 설정 값을 저장
        with(sharedPref.edit()) {
            putBoolean("bus_driver_mode_enabled", false)
            apply()
        }

        // 화면에 메시지 표시
        Toast.makeText(this, "기본 화면으로 전환되었습니다.", Toast.LENGTH_SHORT).show()

        // MainActivity 화면으로 전환
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // 현재 액티비티 종료 (선택 사항)
        finish()
    }
}