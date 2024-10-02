package com.quantuityanalytics.quantuityanalytics.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigationrail.NavigationRailView
import com.quantuityanalytics.quantuityanalytics.R

class SettingsActivity: AppCompatActivity() {

    private val sensorsMainFragment:SettingsSensorsFragment = SettingsSensorsFragment()
    private val filesMainFragment:SettingsFilesFragment = SettingsFilesFragment()
    private val valuesMainFragment:SettingsValuesFragment = SettingsValuesFragment()

    companion object {
        const val TAG = "QuantuityAnalytics.SettingsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val closeButton: FloatingActionButton = findViewById(R.id.nav_rail_fab)
        closeButton.setOnClickListener {
            finish()
        }

        val navigationRail: NavigationRailView = findViewById(R.id.navigationRail)
        navigationRail.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.values -> {
                    loadFragment(R.id.content_fragment, valuesMainFragment)
                    true
                }
                R.id.files -> {
                    loadFragment(R.id.content_fragment, filesMainFragment)
                    true
                }
                R.id.sensors -> {
                    loadFragment(R.id.content_fragment, sensorsMainFragment)
                    true
                }
                else -> false
            }
        }

        loadFragment(R.id.content_fragment, valuesMainFragment)
    }


    private fun loadFragment(frame: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(frame, fragment)
            .commit()
    }
}