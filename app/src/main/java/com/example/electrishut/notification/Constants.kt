@file:JvmName("Constants")

package com.example.electrishut.notification

// Notification Channel Constants
// Name of Notification Channel for verbose notifications of background work
@JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"

const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"

@JvmField val NOTIFICATION_TITLE: CharSequence = "ElectriShut"

const val CHANNEL_ID = "VERBOSE_NOTIFICATION"

const val NOTIFICATION_ID = 1

const val NOTIFICATION_ID2 = 2