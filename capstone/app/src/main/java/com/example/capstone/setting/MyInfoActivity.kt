package com.example.capstone.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.capstone.R
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_my_info.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class MyInfoActivity : AppCompatActivity() {
    var editMode = false
    private lateinit var viewArray: ArrayList<EditText>
    lateinit var intentUserId: String
    lateinit var intentUserName: String
    lateinit var intentUserStudentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        // toolbar 설정
        setSupportActionBar(myinfo_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        viewArray = arrayListOf(MyInfoPhoneText, MyInfoBirthText, MyInfoGradeText, MyInfoClassText, MyInfoNumText, MyInfoEmailText)
    }

    override fun onResume() {
        super.onResume()

        // 성공적으로 intent 전달값을 받았을 경우
        if (intent.hasExtra("user_id")) {
            intentUserId = intent.getStringExtra("user_id")!!
            intentUserName = intent.getStringExtra("user_name")!!
            intentUserStudentId = intent.getStringExtra("user_student_id")!!
        } else {
            // intent 실패할 경우 현재 액티비티 종료
            finish()
        }

        (application as MasterApplication).service.readInfo()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()!!["data"] as LinkedTreeMap<String, String>
                        MyInfoIdText.setText(data["id"])
                        MyInfoNameText.setText(data["name"])
                        MyInfoPhoneText.setText(data["phone"])
                        val birth = data["birth"]?.split(" ")
                        MyInfoBirthText.setText(birth!![0])
                        MyInfoYearText.setText((data["year"] as Double).roundToInt().toString())
                        MyInfoGradeText.setText((data["schoolgrade"] as Double).roundToInt().toString())
                        MyInfoClassText.setText((data["schoolclass"] as Double).roundToInt().toString())
                        MyInfoNumText.setText((data["schoolnumber"] as Double).roundToInt().toString())
                        MyInfoEmailText.setText(data["email"])
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("데이터 로드 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
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
        val intent = Intent(this, SettingActivity::class.java)
        intent.putExtra("user_id", intentUserId)
        intent.putExtra("user_name", intentUserName)
        intent.putExtra("user_student_id", intentUserStudentId)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editmode_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun editModeOnClick(item: MenuItem) {
        if (editMode) {
            item.setIcon(R.drawable.editmode_edit)
            for (view in viewArray) {
                view.isEnabled = false
                view.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.basic)
            }
            updateInfo()
            editMode = !editMode
        } else {
            item.setIcon(R.drawable.editmode_done)
            for (view in viewArray) {
                view.isEnabled = true
                view.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.main_color)
            }
            editMode = ! editMode
        }
    }

    private fun updateInfo() {
        val map = HashMap<String, String>()

        map["phone"] = MyInfoPhoneText.text.toString()
        map["schoolgrade"] = MyInfoGradeText.text.toString()
        map["schoolclass"] = MyInfoClassText.text.toString()
        map["schoolnumber"] = MyInfoNumText.text.toString()
        map["birth"] = MyInfoBirthText.text.toString()
        map["year"] = MyInfoYearText.text.toString()
        map["email"] = MyInfoEmailText.text.toString()

        (application as MasterApplication).service.updateInfo(map)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val tokenMap: LinkedTreeMap<String, String>
                        var accessToken: String? = null
                        var refreshToken: String? = null

                        if (result!!["token"] != null) {
                            tokenMap = result["token"] as LinkedTreeMap<String, String>
                            accessToken = tokenMap["access_token"].toString()
                            refreshToken = tokenMap["refresh_token"].toString()
                        }

                        if (accessToken == null || refreshToken == null) {
                            Toast.makeText(this@MyInfoActivity, "", Toast.LENGTH_LONG).show()
                        } else {
                            // access_token 저장
                            saveUserToken("access_token", accessToken, this@MyInfoActivity)
                            // refresh_token 저장
                            saveUserToken("refresh_token", refreshToken, this@MyInfoActivity)
                            toast("회원정보가 수정되었습니다")
                        }

                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("회원정보 수정에 실패했습니다")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    private fun saveUserToken(name: String, token: String, activity: Activity) {
        val sp = activity.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(name, token)
        editor.apply()
    }
}