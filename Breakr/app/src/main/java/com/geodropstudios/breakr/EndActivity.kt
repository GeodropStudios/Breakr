package com.geodropstudios.breakr

import android.content.Intent
import android.os.Bundle
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

        // Set the end button's onclick.
        findViewById<Button>(R.id.endButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val endText: TextView = findViewById(R.id.endText)
        val actualSessionDuration: Int = intent.getIntExtra("actualSessionDuration", 60)
        val actualNumBreaks: Int = intent.getIntExtra("actualNumBreaks", 0)

        endText.text = resources.getString(R.string.end_template).format(actualSessionDuration, actualNumBreaks)
    }
}