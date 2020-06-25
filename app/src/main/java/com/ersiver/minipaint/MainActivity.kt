package com.ersiver.minipaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = MyCanvasView(this)
        //request the full screen for the layout of myCanvasView
        view.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        view.contentDescription = getString(R.string.canvasContentDescription)
        setContentView(view)


    }
}
