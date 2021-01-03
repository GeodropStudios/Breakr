package com.geodropstudios.breakr

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.*
import android.transition.Fade
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.ceil

class TimerActivity : AppCompatActivity() {

    // References to views
    private var breakTimer: TextView? = null
    private var breakWorkText: TextView? = null
    private var pausePlayButton: Button? = null
    private var popupWindow: PopupWindow? = null
    private var sessionTimer: TextView? = null

    // Variables
    private var breaks: LinkedList<Break>? = null
    private var currentBreakIndex: Int = 0
    private var isCounting: Boolean = true
    private var popupShowing: Boolean = false
    private var savedMillisRemaining: Long = 0
    private var sessionDuration: Int = 60
    private var timer: CountDownTimer? = null
    private var vibrator: Vibrator? = null

    // Constants
    private val minutesToMillis: Long = 60000
    private val millisToMinutes: Double = 1 / minutesToMillis.toDouble()
    private val postBreakDelayMinutes = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the transition animations and the content view.
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Fade()
            exitTransition = Fade()
        }
        setContentView(R.layout.activity_timer)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Create breaks.
        initializeBreaks()

        // Create countdown timer object that counts down the entire session duration and ticks every second.
        timer = object : CountDownTimer(sessionDuration * minutesToMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerOnTick(millisUntilFinished)
            }

            override fun onFinish() {
                timerOnFinish()
            }
        }.start()

        // Set stop button's onclick.
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            startEndActivity(sessionDuration - (savedMillisRemaining * millisToMinutes).toInt() - 1, currentBreakIndex)
        }

        // Set pause/play button's onclick.
        pausePlayButton?.setOnClickListener {
            pausePlay()
            setPausePlayButtonText()
        }

        // Set actual text for placeholders.
        setBreakWorkText()
        setPausePlayButtonText()
        setSessionTimerText(sessionDuration)
    }

    // Initializes the break list by creating breaks at proper intervals.
    private fun initializeBreaks() {
        // Temporary value to later be assigned to breaks.
        val createdBreaks = LinkedList<Break>()

        // Array of all possible types of breaks for comparing priorities and frequencies.
        // Sorted according to priority to later be abel to use indexOf to find highest priority.
        // When adding new break types, make sure to include them here.
        val templateBreaks = arrayOf(ExerciseBreak(0), RestBreak(0), EyeBreak(0)).sortedBy { it.priority }

        // Iteration variable is the number of minutes from the start of the session.
        // Start iterating at the first minute, so that the session does not begin with a break.
        // Stop iteration when the session end has been passed.
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
                when (templateBreaks[breakIndex]) {
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
                minutes += createdBreaks.last.duration + postBreakDelayMinutes
            }
        }

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
        }, R.integer.fade_duration.toLong())
    }

    private fun formatTime(minutes: Int): String {
        val hours: Int = minutes / 60
        return "%s:%s".format(hours, (minutes % 60).toString().padStart(2, '0'))
    }

    private fun pausePlay() {
        // Toggle counting.
        isCounting = !isCounting

        if (isCounting) { // If the timer is now counting.
            // Create countdown timer object that counts down from the saved time and ticks every second.
            timer = object : CountDownTimer(savedMillisRemaining, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timerOnTick(millisUntilFinished)
                }

                override fun onFinish() {
                    timerOnFinish()
                }
            }.start()
        } else { // If the timer is now paused.
            timer?.cancel()
        }
    }

    private fun onBreak() {
        val currentBreak = breaks?.get(currentBreakIndex)
        currentBreak?.active = true

        doVibration()

        // Update visuals.
        showBreakPopup()
        setBreakWorkText()
    }

    private fun doVibration() {
        vibrator?.vibrate(VibrationEffect.createOneShot(resources.getInteger(R.integer.break_vibrate_duration).toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun nextBreak() {
        // If popup is showing, dismiss the popup.
        if (popupShowing) {
            popupWindow?.dismiss()
            popupShowing = false
        }

        doVibration()

        currentBreakIndex++

        // Update visuals.
        setBreakWorkText()
    }

    private fun setBreakWorkText() {
        if (breaks?.get(currentBreakIndex)?.active as Boolean) {
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

    private fun timerOnTick(millisUntilFinished : Long) {
        // Save the millis each tick in order to be able to start and pause the timer.
        savedMillisRemaining = millisUntilFinished

        // Display the time left in the session.
        val minutesLeft = ceil(millisUntilFinished * millisToMinutes).toInt()
        val elapsedMinutes = sessionDuration - minutesLeft
        setSessionTimerText(minutesLeft)

        val currentBreak = breaks?.get(currentBreakIndex)

        // Determine if current break starts or ends now.
        if (!currentBreak?.active!! && currentBreak.start <= elapsedMinutes && elapsedMinutes < currentBreak.start + currentBreak.duration) { // Current break starts now.
            onBreak()
        } else if (currentBreak.active && elapsedMinutes >= currentBreak.start + currentBreak.duration) { // Current break ends now.
            nextBreak()
        }

        // Determine what time to count towards on the break timer and set that time.
        val breakMillis: Long = if (currentBreak.active) {
            millisUntilFinished - (sessionDuration - (currentBreak.start + currentBreak.duration)) * minutesToMillis
        } else {
            millisUntilFinished - (sessionDuration - currentBreak.start) * minutesToMillis
        }

        breakTimer?.text = formatTime(ceil(breakMillis * millisToMinutes).toInt())
    }

    private fun setSessionTimerText(minutesLeft: Int) {
        sessionTimer?.text = formatTime(minutesLeft)
    }

    // When the timer is done, go to the end activity.
    private fun timerOnFinish() {
        startEndActivity(sessionDuration, breaks?.size as Int)
    }

    // Code based on StackOverflow user Suragh (https://stackoverflow.com/a/50188704).
    private fun showBreakPopup() {
        // Register that the popup is showing.
        popupShowing = true;

        // Inflate the layout of the popup window.
        val inflater: LayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.break_popup, null)
        popupView.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_popup)

        // Create the popup window.
        val width: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable: Boolean = true // Lets taps outside the popup also dismiss it.
        popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow?.animationStyle = R.style.popup_animation

        // Show the popup window.
        // Which view is passed doesn't matter.
        popupWindow?.showAtLocation(popupView.rootView, Gravity.BOTTOM, 0, 0)

        // Set the text for the popup window depending on break type.
        when (breaks?.get(currentBreakIndex)) {
            is EyeBreak -> {
                popupView.findViewById<TextView>(R.id.popupHeading).text = getString(R.string.eye_break_heading)
                popupView.findViewById<TextView>(R.id.popupText).text = getString(R.string.eye_break_info)
            }
            is RestBreak -> {
                popupView.findViewById<TextView>(R.id.popupHeading).text = getString(R.string.rest_break_heading)
                popupView.findViewById<TextView>(R.id.popupText).text = getString(R.string.rest_break_info)
            }
            is ExerciseBreak -> {
                popupView.findViewById<TextView>(R.id.popupHeading).text = getString(R.string.exercise_break_heading)
                popupView.findViewById<TextView>(R.id.popupText).text = getString(R.string.exercise_break_info)
            }
        }

        // Dismiss the popup window when touched.
        popupView.setOnTouchListener(object: View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(popupView: View?, event: MotionEvent?): Boolean {
                popupWindow?.dismiss()
                popupShowing = false
                return true
            }
        })
    }
}