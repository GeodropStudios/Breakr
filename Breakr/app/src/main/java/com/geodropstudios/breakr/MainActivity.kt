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

        // Set the transition animations and the content view.
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Fade()
            exitTransition = Fade()
        }
        setContentView(R.layout.activity_main)

        // Create click listeners for the buttons.
        findViewById<Button>(R.id.endButton).setOnClickListener {
            startTimerActivity(60)
        }

        findViewById<Button>(R.id.hourButton2).setOnClickListener {
            startTimerActivity(120)
        }

        findViewById<Button>(R.id.hourButton3).setOnClickListener {
            startTimerActivity(180)
        }

        findViewById<Button>(R.id.hourButton4).setOnClickListener {
            startTimerActivity(240)
        }
    }

    // Starts the timer activity with a session length given in minutes.
    private fun startTimerActivity(minutes: Int) {
        val nextIntent: Intent = Intent(this, TimerActivity::class.java)
        nextIntent.putExtra("sessionDuration", minutes)
        startActivity(nextIntent)
        finish()
    }
}