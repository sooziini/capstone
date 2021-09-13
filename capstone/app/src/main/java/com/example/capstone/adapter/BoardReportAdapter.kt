package com.example.capstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.BoardReport

class BoardReportAdapter(
    private val reportList: ArrayList<BoardReport>,
    private val inflater: LayoutInflater
): RecyclerView.Adapter<BoardReportAdapter.BoardReportViewHolder>() {

    inner class BoardReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val send_id_view = itemView.findViewById<TextView>(R.id.report_item_sendid)
        val recv_id_view = itemView.findViewById<TextView>(R.id.report_item_recvid)
        val body_view = itemView.findViewById<TextView>(R.id.report_item_body)
        val regDate_view = itemView.findViewById<TextView>(R.id.report_item_regdate)

        fun bind(report: BoardReport) {
            send_id_view.text = report.sendId
            recv_id_view.text = report.recvId
            body_view.text = report.body
            regDate_view.text = report.regDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardReportViewHolder {
        val view = inflater.inflate(R.layout.report_item, parent, false)
        return BoardReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: BoardReportViewHolder, position: Int) {
        holder.bind(reportList[position])
    }
}