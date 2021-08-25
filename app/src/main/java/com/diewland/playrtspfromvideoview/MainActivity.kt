package com.diewland.playrtspfromvideoview

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
    val fps = 5
    private val cloneVideoToImage = object: Runnable {
        override fun run() {
            Log.d(TAG, "clone TextureView to ImageView")
            //
            // TODO add face detection
            //
            imageView.setImageBitmap(textureView.bitmap)
            cloneHandler?.postDelayed(this, 1_000L/fps)
        }
    }

}