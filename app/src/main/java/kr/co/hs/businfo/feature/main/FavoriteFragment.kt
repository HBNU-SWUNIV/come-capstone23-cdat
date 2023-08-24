package kr.co.hs.businfo.feature.main

import android.content.Context
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
    private val favoriteItems: MutableList<String> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.favorite, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.favorite_list)

        // 어댑터 초기화
        favoriteListAdapter = FavoriteListAdapter(requireContext(), favoriteItems)
        recyclerView.adapter = favoriteListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    fun addFavoriteItem(item: String) {
        favoriteItems.add(item)
        Log.d("FavoriteFragment", "아이템 추가됨: $item")

        // 어댑터 업데이트
        favoriteListAdapter.notifyDataSetChanged()
    }
}