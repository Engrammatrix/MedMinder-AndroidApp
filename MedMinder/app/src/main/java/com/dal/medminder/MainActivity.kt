package com.dal.medminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.dal.medminder.MainActivity.Foo.NOT_CHANNEL_ID

class MainActivity : AppCompatActivity() {
    private var TAG: String? = "MainActivity"

    object Foo {
        val NOT_CHANNEL_ID = "MedMinder_Remainder"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    /**
     * Creating a notification channel.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOT_CHANNEL_ID,
                "Remainder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Used to give remainders for the medication"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}