package com.quantuityanalytics.quantuityanalytics

import android.os.Bundle
import android.util.Log
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.quantuityanalytics.quantuityanalytics.adapters.SimulateAdapter
import com.quantuityanalytics.quantuityanalytics.ble.QABleRecord
import android.os.Handler
import android.widget.Toast

class SimulateActivity: AppCompatActivity() {

    companion object{
        const val TAG: String = "QuantuityAnalytics.SimulateActivity"
    }

    private lateinit var viewAdapter: SimulateAdapter
    private lateinit var slider: Slider
    private var simulating = false
    private var breaking = false
    private var handler: Handler = Handler()
    private var runnable: Runnable? = null
    private var delay = 4000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulate)

        slider = findViewById(R.id.slider)
        viewAdapter = SimulateAdapter( "4 values model", this, arrayListOf())

        val gridView: GridView = findViewById(R.id.gridView)
        gridView.adapter = viewAdapter

        val buttonStart = findViewById<MaterialButton>(R.id.btn_start)
        buttonStart.setOnClickListener {
            simulating = !simulating
            if (simulating) {
                buttonStart.text = resources.getText(R.string.button_stop)
                buttonStart.icon = ResourcesCompat.getDrawable(this.resources, R.drawable.baseline_stop_circle_24, null)
                slider.isEnabled = false
                Log.d(TAG, "Starting ${slider.value} simulated sensors")
                simulateSensors()
            } else {
                buttonStart.text = resources.getText(R.string.button_start)
                buttonStart.icon = ResourcesCompat.getDrawable(this.resources, R.drawable.baseline_play_outline_24, null)
                slider.isEnabled = true
                Log.d(TAG, "Stopping ${slider.value} simulated sensors")
                handler.removeCallbacks(runnable!!)
                viewAdapter.removeAllItems()
            }

        }

    }


    override fun onPause() {
        super.onPause()
        viewAdapter.removeAllItems()
        runnable?.let {
            handler.removeCallbacks(it)
        }
    }

    private fun simulateSensors() {
        repeat(slider.value.toInt()) {
            Log.d(TAG, "Starting a simulated sensor")
            viewAdapter.addItem(QABleRecord.getDefaultRecord())
        }

        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())

            breaking = !breaking

            for (i in 1..slider.value.toInt()) {
                if (breaking) {
                    //Toast.makeText(this@SimulateActivity, "This method will run every 10 seconds. BREAKING", Toast.LENGTH_SHORT).show()

                    if (i == slider.value.toInt()) {
                        viewAdapter.updateItem(QABleRecord("","","","","d1",0f,0f), (i-1))
                    } else {
                        viewAdapter.updateItem(QABleRecord("","","","","d3",0f,0f), (i-1))
                    }
                } else {
                    //Toast.makeText(this@SimulateActivity, "This method will run every 10 seconds. NOT BREAKING", Toast.LENGTH_SHORT).show()
                    viewAdapter.updateItem(QABleRecord("","","","","d4",0f,0f), (i-1))
                }
            }




        }.also { runnable = it }, delay.toLong())
    }

}