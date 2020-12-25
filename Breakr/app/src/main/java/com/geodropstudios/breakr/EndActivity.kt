package com.geodropstudios.breakr

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EndActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Fade()
            exitTransition = Fade()
        }

        setContentView(R.layout.activity_end)

        val endButton: Button = findViewById<Button>(R.id.endButton)

        endButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}