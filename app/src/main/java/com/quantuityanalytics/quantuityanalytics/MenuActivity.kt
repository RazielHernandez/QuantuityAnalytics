package com.quantuityanalytics.quantuityanalytics

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.quantuityanalytics.quantuityanalytics.settings.SettingsActivity

class MenuActivity: AppCompatActivity() {

    private var intent: Intent? = null

    private val permissionList = arrayListOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    companion object{
        private const val BLUETOOTH_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val beginButton: Button = findViewById(R.id.btn_test)
        beginButton.setOnClickListener{
            val intent = Intent(this, TestActivityDescription::class.java)
            if (checkPermissions()) {
                startActivity(intent)
            }
        }

        val closeButton: MaterialButton = findViewById(R.id.btn_close)
        closeButton.setOnClickListener{
            this.finishAffinity()
        }

        val settingsButton: MaterialButton = findViewById(R.id.btn_settings)
        settingsButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val realTimeTest: Button = findViewById(R.id.btn_real_time)
        realTimeTest.setOnClickListener {
            intent = Intent(this, RealTimeTestActivity::class.java)
            if (checkPermissions()) {
                startActivity(intent)
            }

        }
    }



    private fun checkPermissions(): Boolean {

        val permissionsNeeded = arrayListOf<String>()

        for (permission in permissionList) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            Log.d("TAG", "Asking ${permissionsNeeded.size} permissions")
            ActivityCompat.requestPermissions(
                this, permissionsNeeded.toTypedArray(), BLUETOOTH_PERMISSION_CODE
            )
            return false
        } else {
            return true
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == BLUETOOTH_PERMISSION_CODE && grantResults.isNotEmpty()) {
            var isGranted = true
            for (element in grantResults) {
                if (element == PackageManager.PERMISSION_DENIED) {
                    Log.d("TAG", "Permission $element was denied")
                    isGranted = false
                }
            }

            if (isGranted) {

                if (intent != null) {
                    startActivity(intent)
                }
            } else {

                var someDenied = false
                for (permission in permissions) {
                    Log.d("TAG", "some 0")
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        Log.d("TAG", "some 1")
                        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                            Log.d("TAG", "some 2")
                            someDenied = true
                        }
                    }
                }

                if (someDenied) {
                    Log.d("TAG", "some denied")
                    showSnackBar("Open Settings and grant necessary permissions")
                    val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    settingsIntent.data = Uri.parse("package:${this.packageName}")
                    this.startActivity(settingsIntent)
                } else {
                    showSnackBar("Permissions were denied")
                }


            }
        }
    }

    private fun showSnackBar(msg: String) {
        val layoutMain: ConstraintLayout = findViewById(R.id.main)
        val snack: Snackbar = Snackbar.make(layoutMain, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(R.color.primary_light))
            .setTextColor(resources.getColor(R.color.white))
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.width = FrameLayout.LayoutParams.FILL_PARENT
        view.layoutParams = params
        snack.show()
    }


}