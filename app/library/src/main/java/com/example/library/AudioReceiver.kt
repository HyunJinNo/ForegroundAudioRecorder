package com.example.library

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.util.TimerTask

internal class AudioReceiver : BroadcastReceiver() {
    companion object {
        internal const val ACTION_RECORD = "ACTION_RECORD"
        internal const val ACTION_STOP = "ACTION_STOP"
        internal const val ACTION_CANCEL = "ACTION_CANCEL"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_RECORD -> {
                when (AudioRecorder.recordingState) {
                    RecordingState.BEFORE_RECORDING -> {
                        Toast.makeText(context, "Recording started.", Toast.LENGTH_SHORT).show()
                        AudioTimer.startTimer(object : TimerTask() {
                            override fun run() {
                                NotificationGenerator.notifyNotification(
                                    context, R.layout.custom_notification_recording
                                )
                            }
                        })
                        AudioRecorder.startRecording()
                    }
                    RecordingState.ON_RECORDING -> {
                        Toast.makeText(context, "Recording paused.", Toast.LENGTH_SHORT).show()
                        NotificationGenerator.notifyNotification(
                            context, R.layout.custom_notification_pause
                        )
                        AudioTimer.pauseTimer()
                        AudioRecorder.pauseRecording()
                    }
                    RecordingState.PAUSE -> {
                        Toast.makeText(context, "Recording resumed.", Toast.LENGTH_SHORT).show()
                        AudioTimer.resumeTimer(object : TimerTask() {
                            override fun run() {
                                NotificationGenerator.notifyNotification(
                                    context, R.layout.custom_notification_recording
                                )
                            }
                        })
                        AudioRecorder.resumeRecording()
                    }
                }
            }
            ACTION_STOP -> {
                Toast.makeText(context, "Recording stopped.", Toast.LENGTH_SHORT).show()
                AudioTimer.stopTimer()
                NotificationGenerator.notifyNotification(context, R.layout.custom_notification)
                AudioRecorder.stopRecording()
            }
            ACTION_CANCEL -> {
                Toast.makeText(context, "Recording canceled.", Toast.LENGTH_SHORT).show()
                AudioTimer.stopTimer()
                NotificationGenerator.notifyNotification(context, R.layout.custom_notification)
                AudioRecorder.cancelRecording()
            }
            else -> {
                // Do nothing.
            }
        }
    }
}