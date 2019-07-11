package com.ezyindustries.kkmpushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ezyindustries.kkmpushnotification.event.MessageEvent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe


class MainActivity : AppCompatActivity() {

    var channelId: String? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        channelId = getString(R.string.default_notification_channel_id)

        // get data from intent
        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras.get(key)
            }
        }

        // If want subscribe topic
        subscribeButton.setOnClickListener {
            FirebaseMessaging.getInstance().subscribeToTopic("KMM")
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        }

        // generate Token
        logTokenButton.setOnClickListener {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    // Log and toast
                    val msg = getString(R.string.msg_token_fmt, token)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                })
        }
    }

    // Sample show noitification with expandable group
    fun showGroup(view: View){
        val GROUP_KEY_WORK_EMAIL = "com.ezyindustrie.example.KKM"

        val newMessageNotification1 = NotificationCompat.Builder(this@MainActivity, channelId!!)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle("rizky")
            .setContentText("I will join this event...")
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .build()

        val newMessageNotification2 = NotificationCompat.Builder(this@MainActivity, channelId!!)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle("kurniawan")
            .setContentText("thank for the information...")
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .build()

        val summaryNotification = NotificationCompat.Builder(this@MainActivity, channelId!!)
            .setContentTitle("Group Event")
            //set content text to support devices running API level < 24
            .setContentText("Two new messages")
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setStyle(NotificationCompat.InboxStyle())
            //specify which group this notification belongs to
            .setGroup(GROUP_KEY_WORK_EMAIL)
            //set this notification as the summary for the group
            .setGroupSummary(true)
            .build()

        NotificationManagerCompat.from(this).apply {
            notify(10, newMessageNotification1)
            notify(11, newMessageNotification2)
            notify(12, summaryNotification)
        }
    }

    // show notification with button action
    fun showButton(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val builder = NotificationCompat.Builder(this, channelId!!)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle("My notification Button")
            .setContentText("Please click button below!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Open Main Class",
                pendingIntent)
            .build()

        NotificationManagerCompat.from(this).notify(13, builder)
    }

    // show noitification with expandable large text
    fun showLargeText(view: View) {
        var notification = NotificationCompat.Builder(this, channelId!!)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle("Large Text")
            .setContentText("Click this notification with more text")
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.large_icon))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Apply NotificationCompat.InboxStyle to a notification if you want to add multiple short summary lines, such as snippets from incoming emails. This allows you to add multiple pieces of content text that are each truncated to one line, instead of one continuous line of text provided by NotificationCompat.BigTextStyle."))
            .build()
        NotificationManagerCompat.from(this).notify(14, notification)
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    // RECEIVE DATA FROM EVENT BUS
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {/* Do something */
        event.keyword
        startActivity(Intent(this, SecondActivity::class.java))
    };
}
