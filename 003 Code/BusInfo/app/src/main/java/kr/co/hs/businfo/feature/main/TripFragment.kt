package kr.co.hs.businfo.feature.main

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kr.co.hs.businfo.R

class TripFragment : Fragment() {
    private val tripList: MutableList<String> = mutableListOf()
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.trip, container, false)

        val addTripButton: ImageButton = view.findViewById(R.id.add_trip)
        addTripButton.setOnClickListener {
            // Add button clicked, navigate to TripAddInfo activity
            val intent = Intent(activity, TripAddInfo::class.java)
            startActivityForResult(intent, ADD_TRIP_REQUEST_CODE)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.trip_list)
        val adapter = TripListAdapter(tripList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 파이어베이스에서 데이터 가져오기
        db.collection("trips")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val tripName = document.getString("tripName") ?: ""
                    val tripTime = document.getString("tripTime") ?: ""
                    val destination = document.getString("destination") ?: ""
                    // 가져온 데이터를 리스트에 추가
                    tripList.add("Trip Name: $tripName, $destination \nTrip Time $tripTime")
                }
                // 어댑터에 데이터 변경 알림
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        return view
    }

    companion object {
        private const val ADD_TRIP_REQUEST_CODE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TRIP_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Handle data returned from TripAddInfo activity if needed
        }
    }
}