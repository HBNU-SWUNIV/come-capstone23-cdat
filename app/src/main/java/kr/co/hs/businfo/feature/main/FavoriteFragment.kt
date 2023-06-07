package kr.co.hs.businfo.feature.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.co.hs.businfo.R

class FavoriteFragment : Fragment() {
    private lateinit var favoriteListAdapter: FavoriteListAdapter
    private val favoriteItems: MutableList<String> = mutableListOf("Item 1", "Item 2", "Item 3")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.favorite, container, false)

        // RecyclerView 설정
        val recyclerView = view.findViewById<RecyclerView>(R.id.favorite_list)
        favoriteListAdapter = FavoriteListAdapter(requireContext(), favoriteItems)
        recyclerView.adapter = favoriteListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    fun addFavoriteItem(item: String) {
        favoriteItems.add(item)
        Log.d("FavoriteFragment", "아이템 추가됨: $item") // 호출 시점 로그 출력
        favoriteListAdapter.notifyDataSetChanged() // 변경된 내용을 어댑터에 알립니다.
    }
}