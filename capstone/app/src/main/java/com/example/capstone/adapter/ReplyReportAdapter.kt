//package com.example.capstone.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.capstone.dataclass.ReplyReport
//
//class ReplyReportAdapter(
//    private val reportList: ArrayList<ReplyReport>,
//    private val inflater: LayoutInflater
//): RecyclerView.Adapter<ReplyReportAdapter.ReplyReportViewHolder>() {
//
//    inner class ReplyReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//
//        fun bind(report: ReplyReport) {
//
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyReportViewHolder {
//        val view = inflater//.inflate(R.layout.~~~, parent, false)
////        return ReplyReportViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return reportList.size
//    }
//
//    override fun onBindViewHolder(holder: ReplyReportViewHolder, position: Int) {
//        holder.bind(reportList[position])
//    }
//}