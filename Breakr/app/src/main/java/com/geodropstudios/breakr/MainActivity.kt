package com.geodropstudios.breakr

import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Create click listeners for the buttons.
        findViewById<Button>(R.id.hourButton1).setOnClickListener {
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

    override fun onBackPressed() {
        this.moveTaskToBack(true);
    }

    // Starts the timer activity with a session length given in minutes.
    private fun startTimerActivity(minutes: Int) {
        val nextIntent: Intent = Intent(this, TimerActivity::class.java)
        nextIntent.putExtra("sessionDuration", minutes)
        startActivity(nextIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, R.integer.fade_duration.toLong())
    }
}