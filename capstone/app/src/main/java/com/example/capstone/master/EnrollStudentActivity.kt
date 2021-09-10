package com.example.capstone.master

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.FileAdapter
import kotlinx.android.synthetic.main.activity_enroll_student.*
import org.jetbrains.anko.doAsync
import java.io.File

class EnrollStudentActivity : AppCompatActivity() {
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private val EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_student)

        // toolbar 설정
        setSupportActionBar(enroll_student_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        val permissionChk = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionChk != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없을 경우
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE)
        } else {
            // 권한이 있을 경우
            val filePathList = getFiles()

            EnrollStudent_RecyclerView.adapter = FileAdapter(filePathList, LayoutInflater.from(this), this, application)
            EnrollStudent_RecyclerView.layoutManager = LinearLayoutManager(this)
            EnrollStudent_RecyclerView.setHasFixedSize(true)
        }
    }

    private fun getFiles(): ArrayList<String> {
        val filePathList = ArrayList<String>()
        doAsync {
            Log.d("Searching Path", EXTERNAL_STORAGE_PATH)

            File(EXTERNAL_STORAGE_PATH).walk().forEach {
                if (it.extension == "xlsx" || it.extension == "xls") {
                    Log.d("xlsx or xls", it.absolutePath)
                    filePathList.add(it.absolutePath)
                }
            }
        }
        return filePathList
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity2::class.java))
        finish()
    }
}