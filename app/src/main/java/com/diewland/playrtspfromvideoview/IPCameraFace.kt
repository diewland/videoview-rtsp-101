package com.diewland.playrtspfromvideoview

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.Surface
import android.view.TextureView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection

class IPCameraFace( private val rtspURL: String,
                    private val sourceView: TextureView,
                    private val successCallback: (Bitmap, List<Face>, Float) -> Unit,
                    private val failCallback: ((Exception) -> Unit)?=null,
                    private val fps: Int=5,
                    detectFaceDelay: Int=0) {

    // prepare engine
    private var mPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private var detector = FaceDetection.getClient()

    // detect face delay
    private var startTime: Long = 0
    private var delayMs = detectFaceDelay * 1_000L

    fun open(url: String=rtspURL) {
        if (mPlayer != null) return

        // update start time
        startTime = System.currentTimeMillis()

        // setup media player
        mPlayer = MediaPlayer().apply {
            setDataSource(url)
            setSurface(Surface(sourceView.surfaceTexture))
            setOnPreparedListener {
                // start media player
                start()
                // start clone thread
                handler = Handler(Looper.getMainLooper())
                handler?.post(process)
            }
            prepareAsync()
        }
    }
    fun close() {
        if (mPlayer == null) return

        // release media player
        mPlayer?.release()
        mPlayer = null

        // release clone thread
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    // detect face from source
    private val process = object: Runnable {
        val f = fps.toFloat()
        override fun run() {
            // face detection callback
            sourceView.bitmap?.apply {
                val detectFlag = (System.currentTimeMillis() - startTime) > delayMs
                if (detectFlag) {
                    val input = InputImage.fromBitmap(this, 0)
                    detector.process(input)
                        .addOnSuccessListener {
                            successCallback(this, it, f)
                        }
                        .addOnFailureListener {
                            failCallback?.invoke(it)
                        }
                }
                else {
                    successCallback(this, listOf(), f)
                }
            }
            // next frame
            handler?.postDelayed(this, 1_000L/fps)
        }
    }

}