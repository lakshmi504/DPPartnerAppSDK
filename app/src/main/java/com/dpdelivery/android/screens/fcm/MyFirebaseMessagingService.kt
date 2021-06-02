package com.dpdelivery.android.screens.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.screens.jobdetails.TechJobDetailsActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    var message: String? = null

    var data: Map<String, String>? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title; //get title
            val message = remoteMessage.notification!!.body; //get message
            val clickAction = remoteMessage.notification!!.clickAction //get click_action
            data = remoteMessage.data; //get data

            Log.d(TAG, "Notification Title: $title")
            Log.d(TAG, "Notification Body: $message")
            Log.d(TAG, "Notification click_action: $clickAction")

            sendNotification(title, message, clickAction)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */

    private fun sendRegistrationToServer(token: String?) {
        Log.d("Token", token!!)
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendNotification(title: String?, message: String?, clickAction: String?) {
        var intent = Intent(this, TechJobsListActivity::class.java)
        if (clickAction.equals("com.dppartner.android.JobDetails")) {
            intent = Intent(this@MyFirebaseMessagingService, TechJobDetailsActivity::class.java)
            if (data!!.isNotEmpty()) {
                data?.apply {
                    val jobId = get("jobId")
                    intent.putExtra("jobId", jobId!!)
                }
            }
        } else {
            intent = Intent(this, TechJobsListActivity::class.java)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val GROUP_KEY = "com.dpdelivery.android"

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_dp_delivery)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
            )
            .setGroup(GROUP_KEY)
            .setSound(defaultSoundUri)
            .setColor(getColor(R.color.colorBlue))
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        private val TAG = "MyFirebaseMsgService"
    }
}