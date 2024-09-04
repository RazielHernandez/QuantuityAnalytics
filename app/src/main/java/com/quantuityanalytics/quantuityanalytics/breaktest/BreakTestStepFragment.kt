package com.quantuityanalytics.quantuityanalytics.breaktest

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel

class BreakTestStepFragment: Fragment(R.layout.fragment_test_step) {

    private val breakTestViewModel: BreakViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonRepeat: MaterialButton = view.findViewById(R.id.btn_repeat)
        val buttonNext: MaterialButton = view.findViewById(R.id.btn_next)

        buttonRepeat.setOnClickListener {

        }

        buttonNext.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}