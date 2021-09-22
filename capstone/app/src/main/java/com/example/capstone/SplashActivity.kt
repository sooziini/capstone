package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.capstone.main.MainActivity
import com.example.capstone.network.MasterApplication
import com.example.capstone.user.LoginActivity

class SplashActivity : AppCompatActivity() {

    // 2초간 스플래시 화면 보여줌 (ms)
    val SPLASH_VIEW_TIME: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val app = application as MasterApplication

        val intent = if (app.checkIsLogin()) {
            app.getUserToken(false).let { refreshToken ->
                if (refreshToken != null && refreshToken != "") {
                    app.retrofitSetRefreshToken(refreshToken)
                    Intent(this, MainActivity::class.java)
                } else {
                    app.deleteUserToken()
                    Intent(this, LoginActivity::class.java)
                }
            }
        } else {
            Intent(this, LoginActivity::class.java)
        }

        Handler().postDelayed({
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)      // 액티비티 전환 시 애니메이션 무시
            startActivity(intent)
            finish()
        }, SPLASH_VIEW_TIME)
    }
}