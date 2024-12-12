package com.pachi.smartgreen.fragmets.monitoringfragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pachi.smartgreen.R

class monitoringFragment : Fragment() {

    companion object {
        fun newInstance() = monitoringFragment()
    }

    private val viewModel: MonitoringViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_monitoring, container, false)
    }
}