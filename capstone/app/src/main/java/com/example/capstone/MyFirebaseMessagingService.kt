package com.example.capstone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.capstone.board.BoardDetailActivity
import com.example.capstone.dataclass.NotiPost
import com.example.capstone.network.MasterApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // 새 토큰이 생성될 때마다 onNewToken 콜백 호출
    override fun onNewToken(token: String) {
        // 토큰 값 저장
        val sp = getSharedPreferences("firebase", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("token", token).apply()
    }

    // 메세지 수신 시 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("abc", "onMessageReceived")
            val title = remoteMessage.data["title"].toString()
            val body = remoteMessage.data["body"].toString()
            val board_id = remoteMessage.data["board_id"].toString()

            sendNotification(title, body, board_id)

            val notiList = HashMap<String, ArrayList<NotiPost>>()
            val notiArray = ArrayList<NotiPost>()
            notiArray.add(NotiPost(title, body, board_id.toInt()))
            notiList.put("list", notiArray)

            Log.d("abc", notiList.toString())
            val app = application as MasterApplication
            if (app.isInForeground()) {     // 포그라운드
                app.retrofitCreateNotification(notiList)
            } else {    // 백그라운드
                Log.d("abc", "back")
            }
        }
    }

    fun saveMessage(title: String, body: String, board_id: Int) {
        val sp = getSharedPreferences("notification", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("title", title)
        editor.putString("body", body)
        editor.putInt("board_id", board_id).apply()
        Log.d("abc", "title"+title)
    }

    private fun sendNotification(title: String, body: String, board_id: String) {
        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        // Activity Stack 을 경로만 남김 A-B-C-D-B => A-B
        // 액티비티 중복 생성 방지
        // 이전에 실행된 액티비티들을 모두 없엔 후 새로운 액티비티 실행 플래그
        val intent = Intent(this, BoardDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("board_id", board_id)
            putExtra("activity_num", "0")
        }

        // 일회성 PendingIntent
        // PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임
        val pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT)

        // 알림 채널 이름
        val channelId = getString(R.string.firebase_notification_channel_id)

        // 알림 소리
        // val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 알림에 대한 UI 정보 and 작업 저장
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.cloudy_alpha)             // 아이콘 설정
            .setContentTitle(title)     // 제목
            .setContentText(body)     // 메시지 내용
            .setAutoCancel(true)    // 터치 시 자동 삭제 true
            // .setSound(soundUri) // 알림 소리
            .setContentIntent(pendingIntent)    // 알림 실행 시 Intent
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        notificationManager.notify(uniId, notificationBuilder.build())  // 알림 생성
    }

}
