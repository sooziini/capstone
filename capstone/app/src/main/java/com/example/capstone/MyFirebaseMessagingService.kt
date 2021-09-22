package com.example.capstone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.capstone.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.activityManager

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseService"

    // 새 토큰이 생성될 때마다 onNewToken 콜백 호출
    override fun onNewToken(token: String) {
        // super.onNewToken(token)
        Log.d(TAG, "new Token: $token")

        // 토큰 값 저장
        val sp = getSharedPreferences("firebase", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("token", token).apply()
    }

    // 메세지 수신 시 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"].toString()
            val body = remoteMessage.data["body"].toString()
            Log.d(TAG, body)
            Log.d(TAG, title)
            sendNotification(title, body)
        } else {
            Log.d(TAG, "error")
            Log.d(TAG, remoteMessage.data.toString())
        }
    }

    private fun sendNotification(title: String, body: String) {
        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        // Activity Stack 을 경로만 남김 A-B-C-D-B => A-B
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", title)
            putExtra("Notification", body)
        }
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)     // 액티비티 중복 생성 방지
        // 일회성 PendingIntent
        // PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임
        val pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT)

        // 알림 채널 이름
        val channelId = getString(R.string.firebase_notification_channel_id)

        // 알림 소리
        // val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 알림에 대한 UI 정보 and 작업 저장
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)             // 아이콘 설정
            .setContentTitle(title)     // 제목
            .setContentText(body)     // 메시지 내용
            .setAutoCancel(true)    // 터치 시 자동 삭제 true
            // .setSound(soundUri) // 알림 소리
            .setContentIntent(pendingIntent)    // 알림 실행 시 Intent

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        notificationManager.notify(uniId, notificationBuilder.build())  // 알림 생성
    }

}
