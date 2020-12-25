package com.geodropstudios.breakr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Fade()
            exitTransition = Fade()
        }

        setContentView(R.layout.activity_main)

        val hourButton1: Button = findViewById<Button>(R.id.endButton)
        val hourButton2: Button = findViewById<Button>(R.id.hourButton2)
        val hourButton3: Button = findViewById<Button>(R.id.hourButton3)
        val hourButton4: Button = findViewById<Button>(R.id.hourButton4)

        hourButton1.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }

        hourButton2.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }

        hourButton3.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }

        hourButton4.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }
    }
}