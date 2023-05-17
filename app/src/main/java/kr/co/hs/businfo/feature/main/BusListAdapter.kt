package kr.co.hs.businfo.feature.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.co.hs.businfo.R

class BusListAdapter(
    private val context: Context,
    private val busList: List<String>
) : RecyclerView.Adapter<BusListAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.bus_number_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val busNumber = busList[position]
        holder.bind(busNumber)

        // 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(busNumber)
        }
    }

    override fun getItemCount(): Int {
        return busList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val busNumberTextView: TextView = view.findViewById(R.id.tvBusNumber)

        fun bind(busNumber: String) {
            busNumberTextView.text = busNumber
        }
    }

    interface OnItemClickListener {
        fun onItemClick(busNumber: String)
    }
}