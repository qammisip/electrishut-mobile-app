package com.example.electrishut.dashboard

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.electrishut.MainActivity
import com.example.electrishut.R
import com.example.electrishut.databinding.FragmentDashboardBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ForegroundService : Service() {

    private lateinit var auth: FirebaseAuth
    private val channelId = "ForegroundServiceChannel"

    private var isNotificationShown = false

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("electrishut@gmail.com", "%ELECTRIshut2023!")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    uid?.let { uid ->
                        GlobalScope.launch {
                            fetchSensorData(uid)
                        }
                    }
                } else {
                    Log.e("ForegroundService", "Authentication failed: ${task.exception}")
                }
            }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentText("System is running.")
            .setContentTitle("ElectriShut")
            .setSmallIcon(R.drawable.applogo)
            .build()

        startForeground(1001, notification)

        createNotificationChannel()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun fetchSensorData(uid: String) {
        val sensorDB = FirebaseDatabase.getInstance().reference
        val sensorRef = sensorDB.child("SensorsData").child(uid)

        sensorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val sensorData = snapshot.getValue(SensorsData::class.java)
                    if (sensorData != null) {
                        if (sensorData.waterstat == "CRITICAL" ||
                            sensorData.earthquakestat == "CRITICAL" ||
                            sensorData.firestat == "CRITICAL") {
                            makeStatusNotification("System is in CRITICAL state.")

                            isNotificationShown = false
                        }
                        else if (sensorData.waterstat == "WARNING" ||
                            sensorData.earthquakestat == "WARNING" ||
                            sensorData.firestat == "WARNING") {
                            makeStatusNotification("System is in WARNING state.")

                            isNotificationShown = false
                        }
                        else if (sensorData.waterstat == "NORMAL" ||
                            sensorData.earthquakestat == "NORMAL" ||
                            sensorData.firestat == "NORMAL") {
                            if (!isNotificationShown) {
                                makeStatusNotification("System is running.")
                                isNotificationShown = true
                            }
                        }
                        else {
                            // Do Nothing
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ForegroundService", "Failed to read value.", databaseError.toException())
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Verbose Notifications"
            val description = "Shows verbose notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun makeStatusNotification(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Verbose Notifications"
            val description = "Shows verbose notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("ForegroundServiceChannel", name, importance)
            channel.description = description

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, "ForegroundServiceChannel")
            .setSmallIcon(R.drawable.applogo)
            .setContentTitle("ElectriShut")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1001, builder.build())
    }
}