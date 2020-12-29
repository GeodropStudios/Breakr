package com.geodropstudios.breakr

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.StringBuilder
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

    // Initializes the break list by creating breaks at proper intervals.
    private fun initializeBreaks() {
        // Temporary value to later be assigned to breaks.
        val createdBreaks = LinkedList<Break>()

        // Array of all possible types of breaks for comparing priorities and frequencies.
        // Sorted according to priority to later be abel to use indexOf to find highest priority.
        // When adding new breaks, make sure to include it here.
        val templateBreaks = Array(3) { ExerciseBreak(0); RestBreak(0); EyeBreak(0) }.sortedBy { it.priority }

        // Iteration variable is the number of minutes from the start of the session.
        // Start iterating at the first minute, so that the session does not begin with a break.
        // Stop iteration when the session end has ben passed.
        var minutes = 1
        while (minutes < sessionDuration) {
            // Boolean array saying if the break at index i of templateBreaks could occur at the current minute.
            val possibleBreakIndices = Array(templateBreaks.size) { i -> minutes % templateBreaks[i].frequency == 0 }

            // Find the first index of a break that could appear this minute.
            // The first occurrence will necessarily be the one with highest priority because of sorting earlier.
            // Only consider breaks that are possible this minute and if there are multiple candidates, only choose the one with highest priority.
            val breakIndex = possibleBreakIndices.indexOf(true);

            if (breakIndex < 0) { // If an index was not found, move on to next minute.
                minutes++
            } else { // If an index was found.
                // Determine the type of the selected break and add a break of that type to the break list.
                // Make the new break start at the current minute.
                // If it says the cast is not needed, ignore it.
                when (templateBreaks[breakIndex] as Break) {
                    is ExerciseBreak -> {
                        createdBreaks.add(ExerciseBreak(minutes))
                    }
                    is RestBreak -> {
                        createdBreaks.add(RestBreak(minutes))
                    }
                    is EyeBreak -> {
                        createdBreaks.add(EyeBreak(minutes))
                    }
                }
                // Increment the minutes by the break's duration to avoid overlapping breaks.
                minutes += createdBreaks.last.duration
            }
        }

        val debugString = StringBuilder()
        debugString.append("\n")
        for (createdBreak in createdBreaks) {
            debugString.append(createdBreak)
            debugString.append("\n")
        }
        Log.e("initializeBreaks", debugString.toString())

        // Assign the breaks.
        breaks = createdBreaks
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