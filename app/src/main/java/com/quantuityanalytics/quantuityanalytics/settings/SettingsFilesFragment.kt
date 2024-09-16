package com.quantuityanalytics.quantuityanalytics.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.storage.StorageFilesFragment
import com.quantuityanalytics.quantuityanalytics.storage.StorageRecordsFragment
import com.quantuityanalytics.quantuityanalytics.viewmodel.StorageViewModel


class SettingsFilesFragment: Fragment(R.layout.fragment_settings_files) {

    private val viewModel: StorageViewModel by viewModels()

    private val filesFragment: StorageFilesFragment = StorageFilesFragment()
    private val recordsFragment: StorageRecordsFragment = StorageRecordsFragment()

    companion object {
        const val TAG = "QuantuityAnalytics.SettingsFilesFragment"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFragmentInChild(R.id.files_fragment, filesFragment)
        loadFragmentInChild(R.id.records_fragment, recordsFragment)
    }

    private fun loadFragmentInChild(frame: Int, fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(frame, fragment)
            .commit()
    }

}