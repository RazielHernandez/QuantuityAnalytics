package com.quantuityanalytics.braketest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TestBeginActivity: AppCompatActivity() {

    companion object {
        const val TAG: String = "BrakeTest.TestBeginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_break_test_start)


    }
}