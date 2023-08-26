package kr.co.hs.businfo.feature.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.co.hs.businfo.R
import kr.co.hs.businfo.viewmodel.BusStationViewModel
class FavoriteFragment : Fragment() {
    private lateinit var favoriteListAdapter: FavoriteListAdapter
    private lateinit var busStationViewModel: BusStationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.favorite, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.favorite_list)

        busStationViewModel = ViewModelProvider(requireActivity()).get(BusStationViewModel::class.java)

        favoriteListAdapter = FavoriteListAdapter(requireContext(), busStationViewModel.favoriteItems.value?.toMutableList() ?: mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favoriteListAdapter

        busStationViewModel.favoriteItems.observe(viewLifecycleOwner, Observer {
            favoriteListAdapter.updateData(it)
        })

        return view
    }
}