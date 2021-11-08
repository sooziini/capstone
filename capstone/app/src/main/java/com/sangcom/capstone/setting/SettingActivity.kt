package com.sangcom.capstone.setting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sangcom.capstone.R
import com.sangcom.capstone.main.MainActivity
import com.sangcom.capstone.network.MasterApplication
import com.sangcom.capstone.user.LoginActivity
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

    lateinit var studentId: String          // 아이디
    lateinit var studentName: String        // 이름
    lateinit var studentGradeId: String     // 학번
    private lateinit var BASE_URL: String
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    lateinit var uriPath: Uri
    lateinit var view: ImageView
    var ver: Boolean = false    // 프로필 사진X = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        view = findViewById(R.id.setting_profile)
        val app = application as MasterApplication
        BASE_URL = app.BASE_URL
        studentId = app.getUserInfo("studentId")!!
        studentName = app.getUserInfo("studentName")!!
        studentGradeId = app.getUserInfo("studentGradeId")!!

        // toolbar 설정
        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거
    }

    override fun onResume() {
        super.onResume()

        setting_name.setText(studentName).toString()
        setting_id.setText(studentId).toString()

        val userGrade = studentGradeId.substring(0, 1).toInt().toString()
        val userClass = studentGradeId.substring(1, 3).toInt().toString()
        val userNumber = studentGradeId.substring(3, 5).toInt().toString()
        setting_student_id1.setText(userGrade).toString()
        setting_student_id2.setText(userClass).toString()
        setting_student_id3.setText(userNumber).toString()

        // 기존 프로필 사진 설정
        retrofitGetUserProfile()

        // 프로필 사진 변경
        SettingChangeProfileLayout.setOnClickListener {
            setChangeProfileDialog()
        }

        // 비밀번호 변경
        SettingChangePasswordLayout.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
            finish()
        }

        // 내 정보
        SettingMyInfoLayout.setOnClickListener {
            startActivity(Intent(this, MyInfoActivity::class.java))
            finish()
        }

        // 회원탈퇴
        SettingUserDeleteLayout.setOnClickListener {
            setUserDialog(true)
        }

        // 게시판 신고 목록 조회
        SettingBoardReportLayout.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("reportType", "board")
            startActivity(intent)
            finish()
        }

        // 댓글 신고 목록 조회
        SettingReplyReportLayout.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("reportType", "reply")
            startActivity(intent)
            finish()
        }

        // 개발자에게 피드백 전송
        SettingFeedbackLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO) // 메일 전송 설정
                .apply {
                    type = "text/plain"
                    data = Uri.parse("mailto:") // 이메일 앱에서만 인텐트 처리되도록 설정
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("soojinpar1026@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "상콤 애플리케이션 피드백")
                    putExtra(Intent.EXTRA_TEXT, "자세한 피드백 무조건 환영합니다:))")
                }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(intent, "메일 전송하기"))
            } else {
                toast("메일을 전송할 수 없습니다")
            }
        }

        // 개인정보처리방침
        SettingAppInfoLayout.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
            finish()
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
        (application as MasterApplication).service.getUserProfile(studentId)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.code() == 204) {   // 프로필 사진이 없을 경우
                        ver = false
                    } else {
                        if (response.isSuccessful && response.body()!!["success"] == "true") {
                            ver = true
                            val profileImg = response.body()!!["path"]
                            val profileUri = Uri.parse(BASE_URL + profileImg)
                            setProfile(profileUri)
                        } else {
                            toast("프로필 사진을 조회할 수 없습니다")
                            finish()
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
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialog_board_text)
        dialogText.text = "프로필 사진을 변경하시겠습니까?"

        builder.setPositiveButton("확인") { _, _ ->
            permissionCheck()
        }
            .setNegativeButton("취소", null)
        if (ver)
            builder.setNeutralButton("삭제") { _, _ ->
                retrofitDeleteUserProfile()
            }
        builder.setView(dialogView).show()
    }

    // 권한 체크하는 함수
    private fun permissionCheck() {
        val permissionChk = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permissionChk != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없을 경우
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            // 권한이 있을 경우
            getImages()
        }
    }

    // 갤러리에서 이미지 선택하도록 갤러리로 화면 전환하는 함수 (단일 선택)
    private fun getImages() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.type = "image/*"
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

    // 갤러리에서 이미지 선택 후 실행되는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (resultCode == RESULT_OK) {
                uriPath = data!!.data!!
                val filePath = getImageFilePath(uriPath)
                retrofitCreateUserProfile(filePath)
            } else if (resultCode == RESULT_CANCELED) {
                // 사진 선택 취소
            }
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
                        ver = true
                        toast("프로필 사진을 설정했습니다")
                    } else {
                        toast("프로필 사진을 설정할 수 없습니다")
                        finish()
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
                        ver = false
                        toast("프로필 사진이 삭제되었습니다")
                    } else {
                        toast("프로필 사진을 삭제할 수 없습니다")
                        finish()
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main2_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.main2_menu_logout -> {
                setUserDialog(false)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 회원탈퇴 & 로그아웃 다이얼로그 설정 함수
    // ver == true 회원탈퇴 dialog
    // ver == false 로그아웃 dialog
    private fun setUserDialog(ver: Boolean) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialog_board_text)
        dialogText.text = if (ver) "회원을 탈퇴하시겠습니까?" else "로그아웃 하시겠습니까?"

        builder.setPositiveButton("확인") { _, _ ->
            if (ver) retrofitDeleteUser()
            else {
                (application as MasterApplication).retrofitDeleteDeviceToken()
                retrofitLogout()
            }
        }
            .setNegativeButton("취소", null)
        builder.setView(dialogView)
        builder.show()
    }

    // 회원탈퇴하는 함수
    private fun retrofitDeleteUser() {
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
                        toast("회원탈퇴를 할 수 없습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 로그아웃하는 함수
    private fun retrofitLogout() {
        val app = application as MasterApplication
        app.service.logout()
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        app.deleteUserInfo()
                        app.createRetrofit(null)
                        startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
                        finish()
                        toast("로그아웃 되었습니다")
                    } else {
                        toast("로그아웃을 할 수 없습니다")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}