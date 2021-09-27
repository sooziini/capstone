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
    private val inflater: LayoutInflater,
    private val role: String
): RecyclerView.Adapter<BoardReportAdapter.BoardReportViewHolder>() {
    inner class BoardReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val send_id_view = itemView.findViewById<TextView>(R.id.report_item_sendid)
        val recv_id_view = itemView.findViewById<TextView>(R.id.report_item_recvid)
        val body_view = itemView.findViewById<TextView>(R.id.report_item_body)
        val regDate_view = itemView.findViewById<TextView>(R.id.report_item_regdate)
        val recv_id_text = itemView.findViewById<TextView>(R.id.report_item_recvid_text)

        fun bind(report: BoardReport) {
            if (reportList.size == 0)
                return

            send_id_view.text = report.sendId
            body_view.text = report.body
            regDate_view.text = report.regDate
            if (role == "master")
                recv_id_view.text = report.recvId
            else {
                recv_id_text.visibility = View.INVISIBLE
                recv_id_view.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardReportViewHolder {
        val view = if (reportList.size != 0)
            inflater.inflate(R.layout.report_item, parent, false)
        else inflater.inflate(R.layout.report_null_item, parent, false)
        return BoardReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(reportList.size == 0)
            1
        else reportList.size
    }

    override fun onBindViewHolder(holder: BoardReportViewHolder, position: Int) {
        if (reportList.size != 0)
            holder.bind(reportList[position])
    }
}