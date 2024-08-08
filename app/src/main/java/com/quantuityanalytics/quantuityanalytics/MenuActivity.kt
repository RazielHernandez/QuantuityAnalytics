package com.quantuityanalytics.quantuityanalytics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MenuActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val beginButton: Button = findViewById(R.id.btn_test)
        beginButton.setOnClickListener{
            val intent = Intent(this, TestDescriptionActivity::class.java)
            startActivity(intent)
        }

        val closeButton: Button = findViewById(R.id.btn_close)
        closeButton.setOnClickListener{
            this.finishAffinity()
        }
    }
}