package com.ezyindustries.kkmpushnotification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.ezyindustries.kkmpushnotification.MainActivity
import com.ezyindustries.kkmpushnotification.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.graphics.drawable.Drawable
import androidx.annotation.NonNull
import com.bumptech.glide.request.target.CustomTarget
import android.R.attr.path
import android.graphics.BitmapFactory
import androidx.annotation.Nullable
import com.bumptech.glide.load.engine.GlideException
import com.ezyindustries.kkmpushnotification.event.MessageEvent
import org.greenrobot.eventbus.EventBus


class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        // get data from data notification object
        remoteMessage?.data?.isNotEmpty()?.let {
            var messageData = remoteMessage?.data?.get("rumahId")
            var messageId = messageData
            EventBus.getDefault().post(MessageEvent(messageId!!, 1))
        }

        // get data from  notification object and only send message body and title
        remoteMessage?.notification?.let {
            sendNotification(remoteMessage?.notification?.title!!, remoteMessage?.notification?.body!!)
        }



    }


    override fun onNewToken(token: String?) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }

    private fun sendNotification(title: String,messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.large_icon)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(null))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("To make the image appear as a thumbnail only while the notification is collapsed (as shown in figure 1), call setLargeIcon() and pass it the image, but also call BigPictureStyle.bigLargeIcon() and pass it null so the large icon goes away when the notification is expanded:"))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "EVENT",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // show push notification in status bar
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}
