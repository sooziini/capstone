package com.example.capstone.adapter

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.network.MasterApplication
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class FileAdapter(
    private val fileList: ArrayList<String>,
    private val inflater: LayoutInflater,
    private val context: Context,
    private val application: Application
): RecyclerView.Adapter<FileAdapter.FilePathViewHolder>() {
    inner class FilePathViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val filepath: TextView = itemView.findViewById(R.id.FilePath_TextView)

        fun bind(path: String) {
            if (fileList.size == 0) {
                filepath.text = "엑셀파일이 존재하지 않습니다."
                filepath.gravity = Gravity.CENTER
                return
            }
            else filepath.text = "   $path"

            filepath.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("해당 파일로 등록하시겠습니까?")
                builder.setIcon(R.drawable.ic_add_student)
                builder.setMessage(path)

                val listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            DialogInterface.BUTTON_POSITIVE ->
                                selectFile(path)
                            DialogInterface.BUTTON_NEGATIVE -> // 취소 버튼
                                return
                        }
                    }
                }
                builder.setPositiveButton("확인", listener)
                builder.setNegativeButton("취소", listener)
                builder.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilePathViewHolder {
        val view: View = inflater.inflate(R.layout.filepath_item, parent, false)
        return FilePathViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (fileList.size == 0)
            return 1

        return fileList.size
    }

    override fun onBindViewHolder(holder: FilePathViewHolder, position: Int) {
        if (fileList.size != 0) holder.bind(fileList[position])
        else holder.bind("")
    }

    private fun selectFile(path: String) {
        val file = File(path)
        val requestBody = RequestBody.create(MediaType.parse("application/vnd.msexcel"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)

        (application as MasterApplication).service.uploadAuthFile(body)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        context.toast("업로드 완료")
                    } else {        // 3xx, 4xx 를 받은 경우
                        context.toast("업로드 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    context.toast("network error")
                }
            })
    }
}