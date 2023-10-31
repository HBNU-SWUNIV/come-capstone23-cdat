package kr.co.hs.businfo.feature.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.co.hs.businfo.R

class TripListAdapter(private val tripList: List<String>) : RecyclerView.Adapter<TripListAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_item, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val tripItem = tripList[position]
        holder.bind(tripItem)
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tripTextView: TextView = itemView.findViewById(R.id.trip_name)

        fun bind(tripItem: String) {
            tripTextView.text = tripItem
        }
    }
}
