package kr.co.hs.businfo.feature.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kr.co.hs.businfo.R

class TripAddInfo : AppCompatActivity() {
    private lateinit var tripNameEditText: EditText
    private lateinit var destinationEditText: EditText
    private lateinit var hourSpinner: Spinner
    private lateinit var minuteSpinner: Spinner
    private lateinit var tripImageView: ImageView
    private val PICK_IMAGE = 1

    // 여행 정보를 저장할 변수
    private var tripName: String = ""
    private var tripTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trip_addinfo)

        // UI 요소 초기화
        tripNameEditText = findViewById(R.id.trip_name)
        destinationEditText = findViewById(R.id.destination)
        hourSpinner = findViewById(R.id.hourSpinner)
        minuteSpinner = findViewById(R.id.minuteSpinner)
        tripImageView = findViewById(R.id.trip_image)

        // 이미지 추가 버튼 리스너 설정
        val btnAddTripImage: Button = findViewById(R.id.btnAddtripImage)
        btnAddTripImage.setOnClickListener {
            // 갤러리에서 이미지를 선택하는 인텐트 생성
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"

            // 갤러리 앱 실행
            startActivityForResult(galleryIntent, PICK_IMAGE)
        }

        // 완료 버튼 리스너 설정
        val btnComplete: Button = findViewById(R.id.btnComplete)
        btnComplete.setOnClickListener {
            // 사용자가 입력한 여행 정보 가져오기
            tripName = tripNameEditText.text.toString()
            val hour = hourSpinner.selectedItem.toString()
            val minute = minuteSpinner.selectedItem.toString()
            tripTime = "$hour 시간 $minute 분"
            val destination = destinationEditText.text.toString()

            // Firestore에 데이터 저장
            val db = Firebase.firestore
            val trip = Trip(tripName, tripTime, destination)

            db.collection("trips")
                .add(trip)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "여행 정보가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()

                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "여행 정보 저장 실패: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // 갤러리에서 선택한 이미지 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            // 선택한 이미지의 URI를 가져와서 이미지 뷰에 설정
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                tripImageView.setImageURI(selectedImageUri)
            }
        }
    }
}

data class Trip(
    val tripName: String = "",
    val tripTime: String = "",
    val destination: String = ""
)
