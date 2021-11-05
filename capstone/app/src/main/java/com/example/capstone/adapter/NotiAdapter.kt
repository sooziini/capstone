package com.example.capstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.Noti

class NotiAdapter (
    private var notiList: ArrayList<Noti>,
    private val inflater: LayoutInflater,
    private val itemClick: (Noti) -> Unit
): RecyclerView.Adapter<NotiAdapter.NotiViewHolder>() {
    inner class NotiViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val notiType: TextView = itemView.findViewById(R.id.noti_item_type)
        private val notiBody: TextView = itemView.findViewById(R.id.noti_item_body)
        private val notiDate: TextView = itemView.findViewById(R.id.noti_item_date)

        fun bind(noti: Noti) {
            notiType.text = noti.type
            notiBody.text = noti.title+": "+noti.body
            notiDate.text = noti.regdate.substring(5, 16)

            itemView.setOnClickListener { itemClick(noti) }
        }
    }

    fun refreshNotiItem(notis: ArrayList<Noti>) {
        notiList = notis
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val view = inflater.inflate(R.layout.noti_item, parent, false)
        return NotiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        holder.bind(notiList[position])
    }

    override fun getItemCount(): Int = notiList.size
}