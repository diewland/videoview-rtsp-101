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
                    private val successCallback: (Bitmap, List<Face>, Int) -> Unit,
                    private val failCallback: ((Exception) -> Unit)?=null,
                    private val fps: Int=5 ) {

    // prepare engine
    private var mPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private var detector = FaceDetection.getClient()

    fun open() {
        if (mPlayer != null) return

        // start media player
        mPlayer = MediaPlayer().apply {
            setDataSource(rtspURL)
            setSurface(Surface(sourceView.surfaceTexture))
            prepare()
            start()
        }

        // start clone thread
        handler = Handler(Looper.getMainLooper())
        handler?.post(process)
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
        override fun run() {
            // face detection callback
            sourceView.bitmap?.apply {
                val input = InputImage.fromBitmap(this, 0)
                detector.process(input)
                    .addOnSuccessListener {
                        successCallback(this, it, fps)
                    }
                    .addOnFailureListener {
                        failCallback?.invoke(it)
                    }
            }
            // next frame
            handler?.postDelayed(this, 1_000L/fps)
        }
    }

}