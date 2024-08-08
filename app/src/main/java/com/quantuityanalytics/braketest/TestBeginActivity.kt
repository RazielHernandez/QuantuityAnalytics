package com.quantuityanalytics.braketest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class TestBeginActivity: AppCompatActivity() {

    companion object {
        const val TAG: String = "BrakeTest.TestBeginActivity"
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
            ActivityCompat.requestPermissions(this@TestBeginActivity,
                permissionList.toTypedArray(), BLUETOOTH_PERMISSION_CODE)
            Log.d(TAG, "Asking for permissions")
        } else {
            Log.d(TAG, "Permissions granted")
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkPermission(permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(this@TestBeginActivity, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this@TestBeginActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this@TestBeginActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}