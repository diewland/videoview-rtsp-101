package com.diewland.playrtspfromvideoview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

const val TAG = "RTSP101"

class MainActivity : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var imageView: ImageView
    private lateinit var btnStartMP: Button
    private lateinit var btnStopMP: Button
    private lateinit var tvFps: TextView

    lateinit var ipCam: IPCameraFace
    lateinit var p: Paint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get elements
        textureView = findViewById(R.id.tv_preview)
        imageView = findViewById(R.id.iv_preview)
        btnStartMP = findViewById(R.id.btn_start_mp)
        btnStopMP = findViewById(R.id.btn_stop_mp)
        tvFps = findViewById(R.id.tv_fps)

        // define paint
        p = Paint()
        p.style = Paint.Style.STROKE
        p.color = Color.YELLOW
        p.strokeWidth = 5f

        // init IPCamFace
        ipCam = IPCameraFace(Config.RTSP_URL, textureView, { bmp, faces, fps ->
            if (faces.isNotEmpty()) {
                val canvas = Canvas(bmp)
                faces.forEach { face ->
                    // update frame color from box size
                    p.color = when {
                        face.boundingBox.width() > 300 -> Color.GREEN
                        else -> Color.YELLOW
                    }
                    canvas.drawRect(face.boundingBox, p)
                }
            }
            imageView.setImageBitmap(bmp)
            tvFps.text = "FPS: $fps"
        }, {
            Log.e(TAG, it.stackTraceToString())
        }, Config.FPS)

        // control MediaPlayer
        btnStartMP.setOnClickListener { ipCam.open() }
        btnStopMP.setOnClickListener { ipCam.close() }
    }

    // ---------- DAY1 CODE ----------
    /*
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
    */

}