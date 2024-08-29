package com.quantuityanalytics.quantuityanalytics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.storage.StorageActivity

class MenuActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val beginButton: Button = findViewById(R.id.btn_test)
        beginButton.setOnClickListener{
            val intent = Intent(this, TestActivityDescription::class.java)
            startActivity(intent)
        }

        val closeButton: Button = findViewById(R.id.btn_close)
        closeButton.setOnClickListener{
            this.finishAffinity()
        }

        val storageButton: MaterialButton = findViewById(R.id.btn_storage)
        storageButton.setOnClickListener{
            val intent = Intent(this, StorageActivity::class.java)
            startActivity(intent)
        }
    }
}