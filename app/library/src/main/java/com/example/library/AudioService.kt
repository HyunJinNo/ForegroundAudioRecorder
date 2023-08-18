package com.example.library

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.widget.Toast

class AudioService : Service() {
    private var audioReceiver: AudioReceiver? = null

    companion object {
        private const val NOTIFICATION_ID = 9999
    }

    override fun onCreate() {
        super.onCreate()

        if (audioReceiver == null) {
            audioReceiver = AudioReceiver()
            val filter = IntentFilter()
            registerReceiver(audioReceiver, filter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Toast.makeText(this, "AudioService started.", Toast.LENGTH_SHORT).show()

        intent?.let {
            if (it.action == null) {
                if (audioReceiver == null) {
                    audioReceiver = AudioReceiver()
                    val filter = IntentFilter()
                    registerReceiver(audioReceiver, filter)
                }
            }
        }

        val notification = NotificationGenerator.generateNotification(
            this, R.layout.custom_notification
        )
        startForeground(NOTIFICATION_ID, notification)

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "AudioService finished.", Toast.LENGTH_SHORT).show()

        if (AudioRecorder.recordingState != RecordingState.ON_RECORDING) {
            Toast.makeText(this, "Recording stopped.", Toast.LENGTH_SHORT).show()
            AudioTimer.stopTimer()
            AudioRecorder.stopRecording()
        }

        audioReceiver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun stopAudioService() {
        Toast.makeText(this, "AudioService finished.", Toast.LENGTH_SHORT).show()

        if (AudioRecorder.recordingState != RecordingState.ON_RECORDING) {
            Toast.makeText(this, "Recording stopped.", Toast.LENGTH_SHORT).show()
            AudioTimer.stopTimer()
            AudioRecorder.stopRecording()
        }

        audioReceiver?.let {
            unregisterReceiver(it)
        }
        
        stopSelf()
    }
}