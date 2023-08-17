package com.example.library

import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

internal object AudioRecorder {
    private var recorder: MediaRecorder? = null
    private var filepath: String = ""
    internal var recordingState = RecordingState.BEFORE_RECORDING

    internal fun startRecording() {
        if (!isExternalStorageWritable()) {
            return
        }

        val sdCard = Environment.getExternalStorageDirectory()
        val dir = sdCard.absolutePath + "/AudioService"
        if (!File(dir).exists()) {
            File(dir).mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        filepath = File(sdCard, "AudioService/${timeStamp}.m4a").absolutePath

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(filepath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("startRecoring()", "prepare() failed")
            }

            start()
        }

        recordingState = RecordingState.ON_RECORDING
    }

    internal fun resumeRecording() {
        recorder?.resume()
        recordingState = RecordingState.ON_RECORDING
    }

    internal fun pauseRecording() {
        recorder?.pause()
        recordingState = RecordingState.PAUSE
    }

    internal fun stopRecording() {
        recorder?.run {
            stop()
            reset()
            release()
        }

        recordingState = RecordingState.BEFORE_RECORDING
        recorder = null
    }

    internal fun cancelRecording() {
        stopRecording()
        File(filepath).delete()
    }

    // Checks if a volume containing external storage is available for read and write.
    private fun isExternalStorageWritable() =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}