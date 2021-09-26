package com.example.capstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.ReplyReport

class ReplyReportAdapter(
    private val reportList: ArrayList<ReplyReport>,
    private val inflater: LayoutInflater,
    private val role: String
): RecyclerView.Adapter<ReplyReportAdapter.ReplyReportViewHolder>() {

    inner class ReplyReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val send_id_view = itemView.findViewById<TextView>(R.id.report_item_sendid)
        val recv_id_view = itemView.findViewById<TextView>(R.id.report_item_recvid)
        val recv_id_text = itemView.findViewById<TextView>(R.id.report_item_recvid_text)
        val body_view = itemView.findViewById<TextView>(R.id.report_item_body)
        val regDate_view = itemView.findViewById<TextView>(R.id.report_item_regdate)

        fun bind(report: ReplyReport) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyReportViewHolder {
        val view = inflater.inflate(R.layout.report_item, parent, false)
        return ReplyReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: ReplyReportViewHolder, position: Int) {
        holder.bind(reportList[position])
    }
}