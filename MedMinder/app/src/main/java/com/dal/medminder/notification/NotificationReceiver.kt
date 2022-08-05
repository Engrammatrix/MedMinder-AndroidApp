package com.dal.medminder.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dal.medminder.MainActivity
import com.dal.medminder.MainActivity.Foo.NOT_CHANNEL_ID
import com.dal.medminder.R

/**
 * This class is used to receive notification intents and push a notification
 */
class NotificationReceiver : BroadcastReceiver() {

    /**
     * Override onReceive method.
     *
     * @param Context Context to push notification from.
     * @param Intent The pending intent from the sender.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("onReceive", "Received notification")

        val toIntent = Intent(context, MainActivity::class.java)
        toIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        //Random number is fine as we are pushing the pending intent to open an activity right away.
        val pendingIntent = PendingIntent.getActivity(context, (0..9999).random(), toIntent, 0)

        // Building the notification
        var notificationBuilder = NotificationCompat.Builder(context, NOT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent?.getStringExtra("TITLE"))
            .setContentText(intent?.getStringExtra("DOSAGE"))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)

        //Random number is fine as we are showing the notification right away.
        notificationManager.notify((0..9999).random(), notificationBuilder.build())
    }
}