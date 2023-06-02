package kr.co.hs.businfo.feature.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.co.hs.businfo.R

class FavoriteListAdapter(private val context: Context, private val favoriteItems: List<String>) : RecyclerView.Adapter<FavoriteListAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val busStopName = favoriteItems[position]
        holder.bind(busStopName)
    }

    override fun getItemCount(): Int {
        return favoriteItems.size
    }

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val busStopNameTextView: TextView = itemView.findViewById(R.id.busstop_name)

        fun bind(busStopName: String) {
            busStopNameTextView.text = busStopName
        }
    }
}