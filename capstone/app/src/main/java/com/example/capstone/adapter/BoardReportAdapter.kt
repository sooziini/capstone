//package com.example.capstone.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.capstone.dataclass.BoardReport
//
//class BoardReportAdapter(
//    private val reportList: ArrayList<BoardReport>,
//    private val inflater: LayoutInflater
//): RecyclerView.Adapter<BoardReportAdapter.BoardReportViewHolder>() {
//
//    inner class BoardReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//
//        fun bind(report: BoardReport) {
//
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardReportViewHolder {
//        val view = inflater//.inflate(R.layout.~~~, parent, false)
////        return BoardReportViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return reportList.size
//    }
//
//    override fun onBindViewHolder(holder: BoardReportViewHolder, position: Int) {
//        holder.bind(reportList[position])
//    }
//}