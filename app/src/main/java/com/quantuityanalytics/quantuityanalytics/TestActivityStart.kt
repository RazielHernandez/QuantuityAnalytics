package com.quantuityanalytics.quantuityanalytics

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.quantuityanalytics.quantuityanalytics.breaktest.BreakTestActivity

class TestActivityStart: AppCompatActivity() {

    companion object {
        const val TAG: String = "QuantuityAnalytics.TestBeginActivity"
        private const val BLUETOOTH_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_break_test_start)

        val menuButton: Button = findViewById(R.id.btn_menu)
        menuButton.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        val startButton: Button = findViewById(R.id.btn_start)
        startButton.setOnClickListener {
            val intent = Intent(this, BreakTestActivity::class.java)
            startActivity(intent)
        }
    }
}