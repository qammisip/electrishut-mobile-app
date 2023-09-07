package com.example.electrishut.dashboard

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.content.BroadcastReceiver

class BroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            val serviceIntent = Intent(context, ForegroundService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}