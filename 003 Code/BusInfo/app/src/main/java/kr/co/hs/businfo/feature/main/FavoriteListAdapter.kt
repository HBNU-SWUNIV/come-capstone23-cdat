package kr.co.hs.businfo.feature.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.co.hs.businfo.R
class FavoriteListAdapter(private val context: Context, private val favoriteItems: MutableList<String>) : RecyclerView.Adapter<FavoriteListAdapter.FavoriteViewHolder>() {
    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(busStopName: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val busStopName = favoriteItems[position]
        holder.bind(busStopName)

        // 체크 박스 상태 변경 이벤트 처리
        holder.checkboxFavorite.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 체크되면 버튼을 보이도록 설정
                holder.buttonAction.visibility = View.VISIBLE

                // 버튼 클릭 시 해당 아이템 삭제
                holder.buttonAction.setOnClickListener {
                    val removedItem = favoriteItems.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)

                    // You can also notify your ViewModel or perform any other necessary operations to remove the item from your data source.
                }
            } else {
                // 체크가 해제되면 버튼을 숨기도록 설정
                holder.buttonAction.visibility = View.GONE
            }
        }
    }


    override fun getItemCount(): Int {
        return favoriteItems.size
    }

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val busStopNameTextView: TextView = itemView.findViewById(R.id.busstop_name)
        val checkboxFavorite: CheckBox = itemView.findViewById(R.id.checkbox_favorite)
        val buttonAction: ImageButton = itemView.findViewById(R.id.button_action)

        fun bind(busStopName: String) {
            busStopNameTextView.text = busStopName
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(busStopName)
            }
        }
    }

    // 데이터 변경을 감지, 어댑터 갱신
    fun updateData(newFavoriteItems: List<String>) {
        favoriteItems.clear()
        favoriteItems.addAll(newFavoriteItems)
        notifyDataSetChanged()
    }
}