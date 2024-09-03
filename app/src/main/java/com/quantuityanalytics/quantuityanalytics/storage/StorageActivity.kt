package com.quantuityanalytics.quantuityanalytics.storage

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.viewmodel.StorageViewModel

class StorageActivity: AppCompatActivity() {

    private val storageViewModel: StorageViewModel by viewModels()

    private val filesFragment: StorageFilesFragment = StorageFilesFragment()
    private val recordsFragment: StorageRecordsFragment = StorageRecordsFragment()
    private val inputFragment: StorageInputFragment = StorageInputFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        loadFragment(R.id.files_fragment, filesFragment)
        loadFragment(R.id.records_fragment, recordsFragment)
        loadFragment(R.id.input_fragment, inputFragment)
    }


    private fun loadFragment(frame: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(frame, fragment)
            .commit()
    }
}