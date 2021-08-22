package com.diewland.playrtspfromvideoview

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView

class MainActivity : AppCompatActivity() {

    lateinit var vvPreview: VideoView
    lateinit var btnStart: Button
    lateinit var btnStop: Button

    // DO NOT SUPPORT USER/PASS IN URL --> rtsp://user:pass@url
    private val rtspUrl = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vvPreview = findViewById(R.id.vv_preview)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener {
            vvPreview.setVideoURI(Uri.parse(rtspUrl));
            // vvPreview.setVideoPath(rtspUrl)
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