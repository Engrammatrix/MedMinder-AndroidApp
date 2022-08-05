package com.dal.medminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity

/**
 * This class is design to handling, i.e, scheduling and cancelling a notification.
 * All methods in this class are static.
 */
class NotificationScheduler {

    companion object {
        private val TAG = "NotificationScheduler"

        /**
         *
         * This method is used to schedule a notification.
         *
         * @param FragmentActivity The context that the notification is scheduled from. For our use-case we are pushing from a fragment.
         * @param Long This is the time (in milliseconds) describing when the notification should be pushed.
         * @param String The title for the notification
         * @param String The description for the notification. Our case it's dosage description.
         * @param Boolean To check if the notification should be repeated again.
         * @param Int Repeat frequency, also know as the interval for the repetition.
         * @param Int A unique request code for the pending intent.
         */
        fun scheduleAlarm(
            activity: FragmentActivity?,
            time: Long,
            name: String,
            dosage: String,
            repeatCheck: Boolean,
            repeatFreq: Int,
            reqCode: Int
        ) {
            var alarmManager =
                activity?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

            val intent = Intent(activity, NotificationReceiver::class.java)
            intent.putExtra("TITLE", name)
            intent.putExtra("DOSAGE", dosage)
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)

            Log.d(TAG, "alarmScheduler: " + reqCode)

            val pendingIntent = PendingIntent.getBroadcast(activity, reqCode, intent, 0)
            if (repeatCheck) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    AlarmManager.INTERVAL_DAY * repeatFreq,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            }
        }

        /**
         *
         * This method is used to cancel a scheduled notification.
         *
         * @param FragmentActivity The context that the notification is scheduled from. For our use-case we are pushing from a fragment.
         * @param Int The unique request code for the pending intent that needs to be cancelled.
         */
        fun cancelAlarm(activity: FragmentActivity?, reqCode: Int) {
            var alarmManager =
                activity?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

            val intent = Intent(activity, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(activity, reqCode, intent, 0)

            alarmManager.cancel(pendingIntent)
        }
    }
}