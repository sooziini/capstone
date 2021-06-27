package com.example.capstone

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_board_write.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardWriteActivity : AppCompatActivity() {

    // 키보드 InputMethodManager 변수 선언
    var imm: InputMethodManager? = null
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_write)

        // toolbar 설정
        setSupportActionBar(board_write_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // 사진 첨부 버튼을 클릭했을 경우
        board_write_camera.setOnClickListener {
            val permissionChk = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

            if (permissionChk != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을 경우
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE)
            } else {
                // 권한이 있을 경우
                getImages()
            }
        }

        // 글쓰기 완료 버튼을 클릭했을 경우
        board_write_btn.setOnClickListener {
            val title = board_write_title.text.toString()
            val body = board_write_body.text.toString()
            val post = HashMap<String, String>()

            if (title == "") {
                toast("제목을 입력해주세요")
            } else if (body == "") {
                toast("내용을 입력해주세요")
            } else {
                post.put("title", title)
                post.put("body", body)

                // 입력받은 title과 body POST
                retrofitCreatePost(post)
            }
        }

    }

    private fun getImages() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setType("image/*")
        startActivityForResult(intent, REQUEST_READ_EXTERNAL_STORAGE)
    }

    // 선택한 이미지 파일의 절대 경로 구하는 함수
    private fun getImageFilePath(contentUri: Uri): String {
        var columnIndex = 0
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        // 파일의 절대 경로 return
        return cursor.getString(columnIndex)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            val uri: Uri = data?.data!!
            filePath = getImageFilePath(uri)

            Log.d("abc", filePath)
        }
    }

    // 입력받은 title과 body POST하는 함수
    private fun retrofitCreatePost(post: HashMap<String, String>) {
        (application as MasterApplication).service.createPost(post)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!.get("success") == "true") {
                        startActivity(Intent(this@BoardWriteActivity, BoardActivity::class.java))
                    } else {
                        toast("게시글 작성 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    //finish()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                startActivity(Intent(this, BoardActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 이벤트 메서드 생성
    // 액티비티 최상위 layout에 onClick 세팅
    // 해당 layout 내 view 클릭 시 함수 실행
    fun hideKeyboard(v: View) {
        if (v != null)
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}