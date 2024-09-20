package com.quantuityanalytics.quantuityanalytics.breaktest

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.model.TestStep
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel
import java.lang.StringBuilder

class BreakTestStepFragment: Fragment(R.layout.fragment_test_step) {

    private val breakTestViewModel: BreakViewModel by activityViewModels()
    private var steps: ArrayList<TestStep> = arrayListOf()
    private var actualStep: Int = 0

    companion object {
        const val TAG: String = "QuantuityAnalytics.BreakTestStepFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        steps = TestStep.getTestSteps(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (steps.size == 0) { activity?.finish() }

        val btnRepeat: MaterialButton = view.findViewById(R.id.btn_repeat)
        val btnNext: MaterialButton = view.findViewById(R.id.btn_next)
        val btnTest: MaterialButton = view.findViewById(R.id.btn_test)

        btnRepeat.setOnClickListener {
            Log.d(TAG, "On test repeat button")
            duringTestLayout()
            runStep(steps[actualStep].seconds.toLong())

            for(device in breakTestViewModel.listOfDevices.value!!) {
                device.removeLastRecord()
            }
        }

        btnNext.setOnClickListener {
            actualStep += 1
            if (actualStep == steps.size ) {
                Log.d(TAG, "Test completed")
                breakTestViewModel.setStartAction(false)
                activity?.finish()
            }  else {

                if (actualStep == (steps.size - 1)) {
                    btnNext.text = resources.getText(R.string.btn_save)
                }
                loadStepInfo(steps[actualStep], "Step ${actualStep+1}")
                beforeTestLayout()
            }
        }

        btnTest.setOnClickListener {
            Log.d(TAG, "On test start button")
            duringTestLayout()
            runStep(steps[actualStep].seconds.toLong())
        }

        breakTestViewModel.listOfDevices.observe(viewLifecycleOwner, Observer { list ->
            Log.d(TAG, "Ujuuuu !!! update received")
            val sb = StringBuilder()
            for (element in list) {
                val lastRecord = element.getLastRecord().breakRecord
                sb.append("Sensor ${element.deviceName()} (${element.deviceAddress()}): $lastRecord\n")
                Log.d(TAG, "RESULT: ${sb.toString()}")
            }
            view.findViewById<TextView>(R.id.step_result_text).text = sb.toString()
        })

        loadStepInfo(steps[actualStep], "Step ${actualStep+1}")
        beforeTestLayout()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun runStep(timerLong: Long) {
        Log.d(TAG, "Start timer of $timerLong milliseconds")
        object : CountDownTimer(timerLong, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                Log.d(TAG, "$secondsRemaining second(s) remaining")
            }

            override fun onFinish() {
                breakTestViewModel.setReadAction(true)
                afterTestLayout()
            }
        }.start()
    }

    private fun loadStepInfo(step: TestStep, tittle: String) {
        val title: TextView? = view?.findViewById(R.id.title)
        val image: ImageView? = view?.findViewById(R.id.step_image)
        val description: TextView? = view?.findViewById(R.id.step_description)

        title?.text = tittle
        image?.setImageResource(step.image)
        description?.text = step.description
    }

    private fun beforeTestLayout() {
        view?.findViewById<MaterialButton>(R.id.btn_test)!!.visibility = View.VISIBLE
        view?.findViewById<MaterialButton>(R.id.btn_repeat)!!.visibility = View.GONE
        view?.findViewById<MaterialButton>(R.id.btn_next)!!.visibility = View.GONE
        view?.findViewById<TextView>(R.id.step_result_text)!!.visibility = View.INVISIBLE
    }

    private fun duringTestLayout() {
        view?.findViewById<MaterialButton>(R.id.btn_test)!!.visibility = View.GONE
        view?.findViewById<MaterialButton>(R.id.btn_repeat)!!.visibility = View.GONE
        view?.findViewById<MaterialButton>(R.id.btn_next)!!.visibility = View.GONE
        view?.findViewById<TextView>(R.id.step_result_text)!!.visibility = View.INVISIBLE
    }

    private fun afterTestLayout() {
        view?.findViewById<MaterialButton>(R.id.btn_test)!!.visibility = View.GONE
        view?.findViewById<MaterialButton>(R.id.btn_repeat)!!.visibility = View.VISIBLE
        view?.findViewById<MaterialButton>(R.id.btn_next)!!.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.step_result_text)!!.visibility = View.VISIBLE
    }
}