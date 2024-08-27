package com.quantuityanalytics.quantuityanalytics.storage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface
import com.quantuityanalytics.quantuityanalytics.adapters.StorageFileAdapter
import com.quantuityanalytics.quantuityanalytics.storage.StorageRecordsFragment.Companion
import com.quantuityanalytics.quantuityanalytics.viewmodel.StorageViewModel

class StorageFilesFragment: Fragment(R.layout.fragment_storage_files), RecycleViewItemInterface {

    private val storageViewModel: StorageViewModel by activityViewModels()
    private var localStorageManager: LocalStorageManager? = null

    private var fileAdapter: StorageFileAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var closeButton: Button? = null

    companion object{
        const val TAG: String = "QuantuityAnalytics.StorageFilesFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycleView)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = fileAdapter

        closeButton = view.findViewById(R.id.btn_close)

        view.findViewById<MaterialButton>(R.id.btn_refresh).setOnClickListener {
            loadFiles()
        }

        storageViewModel.update.observe(viewLifecycleOwner, Observer {
            loadFiles()
        })

        closeButton?.setOnClickListener {
            Log.d(StorageRecordsFragment.TAG, "Close activity")
            activity?.finish()
        }

        loadFiles()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fileAdapter = StorageFileAdapter(context, arrayListOf(), this)
        localStorageManager = LocalStorageManager(context)
    }

    private fun loadFiles() {
        localStorageManager?.let {
            fileAdapter?.loadFiles(it.getFiles())
            fileAdapter?.notifyDataSetChanged()
        }
    }

    override fun onDeviceClick(position: Int) {
        fileAdapter?.let {
            storageViewModel.setFileName(it.getFileName(position))
        }
    }

}