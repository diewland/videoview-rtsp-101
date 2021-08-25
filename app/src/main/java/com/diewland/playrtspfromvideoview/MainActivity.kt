package com.diewland.playrtspfromvideoview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector

const val TAG = "RTSP101"

class MainActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var textureView: TextureView
    private lateinit var imageView: ImageView
    private lateinit var btnStartVV: Button
    private lateinit var btnStopVV: Button
    private lateinit var btnStartMP: Button
    private lateinit var btnStopMP: Button
    private lateinit var btnCapture: Button

    private var mPlayer: MediaPlayer? = null
    private var cloneHandler: Handler? = null
    // private lateinit var surface: Surface

    // face detection
    private val fps = 5
    private lateinit var detector: FaceDetector
    lateinit var p: Paint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get elements
        videoView = findViewById(R.id.vv_preview)
        textureView = findViewById(R.id.tv_preview)
        imageView = findViewById(R.id.iv_preview)
        btnStartVV = findViewById(R.id.btn_start_vv)
        btnStopVV = findViewById(R.id.btn_stop_vv)
        btnStartMP = findViewById(R.id.btn_start_mp)
        btnStopMP = findViewById(R.id.btn_stop_mp)
        btnCapture = findViewById(R.id.btn_capture)

        // initial fact detector
        detector = FaceDetection.getClient()

        // define paint
        p = Paint()
        p.style = Paint.Style.STROKE
        p.color = Color.YELLOW
        p.strokeWidth = 5f

        // get MediaPlayer surface
        /*
        textureView.surfaceTextureListener = object: TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
                surface = Surface(texture)
            }
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                // pass
            }
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return true
            }
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                // pass
            }
        }
        */

        // control VideoView
        btnStartVV.setOnClickListener {
            videoView.setVideoURI(Uri.parse(Config.RTSP_URL));
            //videoView.setVideoPath(Config.RTSP_URL)
            //videoView.setZOrderOnTop(false);
            videoView.requestFocus();
            //videoView.postInvalidateDelayed(0);
            videoView.start();
        }
        btnStopVV.setOnClickListener {
            videoView.stopPlayback()
        }

        // control MediaPlayer
        btnStartMP.setOnClickListener {
            // start media player
            mPlayer = MediaPlayer().apply {
                setDataSource(Config.RTSP_URL)
                // setSurface(surface)
                setSurface(Surface(textureView.surfaceTexture))
                prepare()
                start()
            }

            // clone to imageview
            cloneHandler = Handler(Looper.getMainLooper())
            cloneHandler?.post(cloneVideoToImage)
        }
        btnStopMP.setOnClickListener {
            // release media player
            mPlayer?.release()
            mPlayer = null

            // release clone handler
            cloneHandler?.removeCallbacksAndMessages(null)
            cloneHandler = null
        }

        // capture
        btnCapture.setOnClickListener {
            imageView.setImageBitmap(textureView.bitmap)
        }
    }

    // clone TextureView to ImageView
    private val cloneVideoToImage = object: Runnable {
        override fun run() {
            Log.d(TAG, "clone TextureView to ImageView")

            // face detection
            textureView.bitmap?.apply {
                val input = InputImage.fromBitmap(this, 0)
                detector.process(input)
                    .addOnSuccessListener {
                        if (it.isNotEmpty()) {
                            val canvas = Canvas(this)
                            it.forEach { face ->
                                // update frame color from box size
                                p.color = when {
                                    face.boundingBox.width() > 300 -> Color.GREEN
                                    else -> Color.YELLOW
                                }
                                canvas.drawRect(face.boundingBox, p)
                            }
                        }
                        imageView.setImageBitmap(this)
                    }
                    .addOnFailureListener {
                        Log.e(TAG, it.stackTraceToString())
                    }
            }

            // next frame
            cloneHandler?.postDelayed(this, 1_000L/fps)
        }
    }

}