package com.example.capstone

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_setting.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SettingActivity : AppCompatActivity() {

    lateinit var intentUserId: String
    lateinit var intentUserName: String
    lateinit var intentUserStudentId: String
    private lateinit var BASE_URL: String
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    lateinit var uriPath: Uri
    lateinit var view: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        view = findViewById(R.id.setting_profile)
        BASE_URL = (application as MasterApplication).BASE_URL

        // toolbar 설정
        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거
    }

    override fun onResume() {
        super.onResume()
        // 성공적으로 intent 전달값을 받았을 경우
        if (intent.hasExtra("user_id")) {
            intentUserId = intent.getStringExtra("user_id")!!
            intentUserName = intent.getStringExtra("user_name")!!
            intentUserStudentId = intent.getStringExtra("user_student_id")!!

            setting_name.setText(intentUserName).toString()
            setting_id.setText(intentUserId).toString()

            val userGrade = intentUserStudentId.substring(0, 1).toInt().toString()
            val userClass = intentUserStudentId.substring(1, 3).toInt().toString()
            val userNumber = intentUserStudentId.substring(3, 5).toInt().toString()
            setting_student_id1.setText(userGrade).toString()
            setting_student_id2.setText(userClass).toString()
            setting_student_id3.setText(userNumber).toString()

            // 기존 프로필 사진 설정
            retrofitGetUserProfile()
        } else {
            // intent 실패할 경우 현재 액티비티 종료
            finish()
        }

        // 프로필 사진 변경
        SettingChangeProfileLayout.setOnClickListener {
            setChangeProfileDialog()
        }

        // 비밀번호 변경
        SettingChangePasswordLayout.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.putExtra("user_id", intentUserId)
            intent.putExtra("user_name", intentUserName)
            intent.putExtra("user_student_id", intentUserStudentId)
            startActivity(intent)
            finish()
        }

        // 내 정보
        SettingMyInfoLayout.setOnClickListener {
            val intent = Intent(this, MyInfoActivity::class.java)
            intent.putExtra("user_id", intentUserId)
            intent.putExtra("user_name", intentUserName)
            intent.putExtra("user_student_id", intentUserStudentId)
            startActivity(intent)
            finish()
        }

        // 회원탈퇴
        SettinguserDeleteLayout.setOnClickListener {
            setUserDeleteDialog()
        }

    }

    // 이미지 로드
    fun setProfile(profileUri: Uri) {
        val multiOption = MultiTransformation(CenterCrop(), RoundedCorners(20))
        Glide.with(this@SettingActivity)
            .load(profileUri)
            .apply(RequestOptions.bitmapTransform(multiOption))
            .placeholder(R.drawable.profile_cloud)
            .into(view)
    }

    // 프로필 사진 조회하는 함수
    private fun retrofitGetUserProfile() {
        (application as MasterApplication).service.getUserProfile(intentUserId)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.code() == 204) {
                        // 프로필 사진이 없을 경우
                    } else {
                        if (response.isSuccessful && response.body()!!["success"] == "true") {
                            val profileImg = response.body()!!["path"]
                            val profileUri = Uri.parse(BASE_URL+profileImg)
                            setProfile(profileUri)
                        } else {
                            toast("프로필 사진 조회 실패")
                        }
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 프로필 사진 변경 다이얼로그 설정하는 함수
    private fun setChangeProfileDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialog_board_text)
        dialogText.text = "프로필 사진을 변경하시겠습니까?"

        builder.setPositiveButton("확인") { dialog, it ->
            permissionCheck()
        }
            .setNegativeButton("취소", null)
            .setNeutralButton("삭제") { dialog, it ->
                retrofitDeleteUserProfile()
            }
        builder.setView(dialogView)
        builder.show()
    }

    // 권한 체크하는 함수
    private fun permissionCheck() {
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

    // 갤러리에서 이미지 선택하도록 갤러리로 화면 전환하는 함수 (단일 선택)
    private fun getImages() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setType("image/*")
        startActivityForResult(intent, REQUEST_READ_EXTERNAL_STORAGE)
    }

    // 선택한 이미지 파일의 절대 경로 구하는 함수
    fun getImageFilePath(contentUri: Uri): String {
        var columnIndex = 0
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        // 파일의 절대 경로 return
        return cursor.getString(columnIndex)
    }

    // 갤러리에서 이미지 선택 후 실행되는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            uriPath = data!!.data!!
            val filePath = getImageFilePath(uriPath)
            retrofitCreateUserProfile(filePath)
        }
    }

    // 프로필 사진 설정하는 함수
    private fun retrofitCreateUserProfile(filePath: String) {
        val file = File(filePath)
        val fileRequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("profile", file.name, fileRequestBody)

        (application as MasterApplication).service.createUserProfile(part)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"] == "true") {
                        setProfile(uriPath)
                        toast("프로필 사진을 설정했습니다")
                    } else {
                        toast("프로필 사진 설정 실패")
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 프로필 사진 삭제하는 함수
    private fun retrofitDeleteUserProfile() {
        (application as MasterApplication).service.deleteUserProfile()
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"] == "true") {
                        view.setImageResource(R.drawable.profile_cloud)
                        toast("프로필 사진이 삭제되었습니다")
                    } else {
                        toast("프로필 사진 삭제 실패")
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 회원탈퇴 다이얼로그 설정하는 함수
    private fun setUserDeleteDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialog_board_text)
        dialogText.text = "회원을 탈퇴하시겠습니까?"

        builder.setPositiveButton("확인") { dialog, it ->
            (application as MasterApplication).service.deleteUser()
                .enqueue(object : Callback<HashMap<String, String>> {
                    override fun onResponse(
                        call: Call<HashMap<String, String>>,
                        response: Response<HashMap<String, String>>
                    ) {
                        if (response.isSuccessful) {
                            toast("회원탈퇴가 완료되었습니다")
                            startActivity((Intent(this@SettingActivity, LoginActivity::class.java)))
                            finish()
                        } else {        // 3xx, 4xx 를 받은 경우
                            toast("회원탈퇴 실패")
                        }
                    }

                    // 응답 실패 시
                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                        toast("network error")
                        finish()
                    }
                })
        }
            .setNegativeButton("취소", null)
        builder.setView(dialogView)
        builder.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}