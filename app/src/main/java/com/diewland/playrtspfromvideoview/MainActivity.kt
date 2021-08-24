package com.diewland.playrtspfromvideoview

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView

class MainActivity : AppCompatActivity() {

    private lateinit var vvPreview: VideoView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vvPreview = findViewById(R.id.vv_preview)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener {
            vvPreview.setVideoURI(Uri.parse(Config.RTSP_URL));
            //vvPreview.setVideoPath(Config.RTSP_URL)
            //vvPreview.setZOrderOnTop(false);
            vvPreview.requestFocus();
            //vvPreview.postInvalidateDelayed(0);
            vvPreview.start();
        }
        btnStop.setOnClickListener {
            vvPreview.stopPlayback()
        }
    }

}