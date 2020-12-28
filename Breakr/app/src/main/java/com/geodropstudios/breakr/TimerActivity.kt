package com.geodropstudios.breakr

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class TimerActivity : AppCompatActivity() {

    // References to views
    private var breakTimer: TextView? = null
    private var breakWorkText: TextView? = null
    private var pausePlayButton: Button? = null
    private var sessionTimer: TextView? = null

    // Variables
    private var breaks: LinkedList<Break>? = null
    private var currentBreakIndex: Int = 0
    private var elapsedTime: Int = 0
    private var isCounting: Boolean = true
    private var isOnBreak: Boolean = false
    private var sessionDuration: Int = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the transition animations and the content view.
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Fade()
            exitTransition = Fade()
        }
        setContentView(R.layout.activity_timer)

        initialize()
    }

    override fun onBackPressed() {
        this.moveTaskToBack(true)
    }

    private fun initialize() {
        // Get the session duration from the intent.
        sessionDuration = intent.getIntExtra("sessionDuration", 60)

        // Get reference to views in the activity.
        breakTimer = findViewById(R.id.breakTimer)
        breakWorkText = findViewById(R.id.breakWorkText)
        pausePlayButton = findViewById(R.id.pausePlayButton)
        sessionTimer = findViewById(R.id.sessionTimer)

        // Set stop button's onclick.
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            startEndActivity(elapsedTime, currentBreakIndex)
        }

        // Set pause/play button's onclick.
        pausePlayButton?.setOnClickListener {
            isCounting = !isCounting
            setPausePlayButtonText()
        }

        // Set actual text for placeholders.
        setBreakWorkText()
        setPausePlayButtonText()

        // Create breaks.
        initializeBreaks()
    }

    private fun initializeBreaks() {
        breaks = LinkedList<Break>()

        // bladibla
    }

    // Starts the end activity with a session length given in minutes and a number of breaks.
    private fun startEndActivity(minutes: Int, numBreaks: Int) {
        val nextIntent: Intent = Intent(this, EndActivity::class.java)
        nextIntent.putExtra("actualSessionDuration", minutes)
        nextIntent.putExtra("actualNumBreaks", numBreaks)
        startActivity(nextIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, R.integer.fadeDuration.toLong())
    }

    private fun setBreakWorkText() {
        if (isOnBreak) {
            breakWorkText?.text = resources.getString(R.string.break_text)
        } else {
            breakWorkText?.text = resources.getString(R.string.work_text)
        }
    }

    private fun setPausePlayButtonText() {
        if (isCounting) {
            pausePlayButton?.text = resources.getString(R.string.pause_text)
        } else {
            pausePlayButton?.text = resources.getString(R.string.play_text)
        }
    }
}