package com.quantuityanalytics.braketest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.braketest.adapters.TestDescriptionAdapter
import com.quantuityanalytics.braketest.model.TestDescriptionItem
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class TestDescriptionActivity: AppCompatActivity() {

    companion object {
        val TAG: String = "BrakeTest.TestDescriptionActivity"
    }

    private var viewPager2: ViewPager2? = null
    private val pager2Callback = object:ViewPager2.OnPageChangeCallback(){

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            val controllerButton: MaterialButton = findViewById(R.id.controllerBtn)
            Log.d(TAG, "change to page "+position+" de "+TestDescriptionItem.getTestDescriptionSteps(applicationContext).size)
            if (position == TestDescriptionItem.getTestDescriptionSteps(applicationContext).size -1){
                controllerButton.text = "Start"
            }else{
                controllerButton.text = "Next"
                controllerButton.setOnClickListener {
                    viewPager2?.currentItem = position+1
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test_description)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewPager()

        val menuButton: Button = findViewById(R.id.btn_menu)
        menuButton.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViewPager(){

        val adapter = TestDescriptionAdapter(TestDescriptionItem.getTestDescriptionSteps(applicationContext))
        viewPager2 = findViewById(R.id.viewPager)
        viewPager2?.adapter = adapter
        viewPager2?.registerOnPageChangeCallback(pager2Callback)
        findViewById<DotsIndicator>(R.id.dots_indicator).setViewPager2(viewPager2!!)

    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager2?.unregisterOnPageChangeCallback(pager2Callback)
    }
}