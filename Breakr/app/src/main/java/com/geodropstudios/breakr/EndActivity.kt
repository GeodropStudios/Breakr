package com.geodropstudios.breakr

import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EndActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the transition animations and the content view.
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Fade()
            exitTransition = Fade()
        }
        setContentView(R.layout.activity_end)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Set the end button's onclick.
        findViewById<Button>(R.id.endButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, R.integer.fadeDuration.toLong())
        }

        val endText: TextView = findViewById(R.id.endText)
        val actualSessionDuration: Int = intent.getIntExtra("actualSessionDuration", 60)
        val actualNumBreaks: Int = intent.getIntExtra("actualNumBreaks", 0)

        endText.text = resources.getString(R.string.end_template).format(actualSessionDuration, if (actualSessionDuration != 1) "s" else "", actualNumBreaks, if (actualNumBreaks != 1) "s" else "")
    }

    override fun onBackPressed() {
        this.moveTaskToBack(true);
    }
}