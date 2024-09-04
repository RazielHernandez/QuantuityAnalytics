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
            startScanningButton()
        }
    }

    private fun startScanningButton() {

        val permissionList : MutableList<String> = ArrayList()
        if (!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            permissionList.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (!checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            permissionList.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this@TestActivityStart,
                permissionList.toTypedArray(), BLUETOOTH_PERMISSION_CODE)
        } else {
            val intent = Intent(this, BreakTestActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkPermission(permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(this@TestActivityStart, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, BreakTestActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this@TestActivityStart, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}