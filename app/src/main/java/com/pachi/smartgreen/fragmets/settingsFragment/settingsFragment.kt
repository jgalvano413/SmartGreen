package com.pachi.smartgreen.fragmets.settingsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pachi.smartgreen.R
import com.pachi.smartgreen.adapter.NotificationAdapter
import com.pachi.smartgreen.objets.ItemSpacingDecoration
import com.pachi.smartgreen.objets.NotificationSettings

class settingsFragment : Fragment() {

    private lateinit var root: View
    private lateinit var notificationSettings: NotificationSettings
    private var checkData = false
    private val dataList = mutableListOf<Any>()
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getData()
        configNotifiaction()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_settings, container, false)
        return root
    }

    private fun configNotifiaction(){
        notificationSettings = NotificationSettings(requireContext())
        setChechbox()
    }

    private fun setSttings(){
        val process = root.findViewById<ProgressBar>(R.id.progressBar)
        process.visibility = View.GONE
        val recycler = root.findViewById<RecyclerView>(R.id.recyclerViewSettings)
        recycler.visibility = View.VISIBLE
        val adapter = NotificationAdapter(dataList)
        recycler.setLayoutManager(LinearLayoutManager(requireContext()))
        recycler.adapter = adapter
        if (!checkData) {
            recycler.addItemDecoration(ItemSpacingDecoration(45))
            checkData = true
        }
    }


    private fun getObjets(){
        if (viewModel.getList().isNotEmpty()){
            setSttings()
        } else {
            viewModel.process.observe(viewLifecycleOwner, Observer { process ->
                if (process){
                    dataList.clear()
                    dataList.addAll(viewModel.getList())
                    setSttings()
                    viewModel.process.hasObservers()
                }
            })
        }
    }

    private fun setChechbox(){

        val check = root.findViewById<CheckBox>(R.id.checkbox_enable_feature)
        check.isChecked = notificationSettings.getNotificationPermiss()
        if (notificationSettings.getNotificationPermiss()) root.findViewById<View>(R.id.notificationView).visibility = View.VISIBLE
        else root.findViewById<View>(R.id.notificationView).visibility = View.GONE
        check.setOnCheckedChangeListener { _, isChecked ->
            notificationSettings.setNotificationPermiass(isChecked)
            if (isChecked) root.findViewById<View>(R.id.notificationView).visibility = View.VISIBLE
            else root.findViewById<View>(R.id.notificationView).visibility = View.GONE
        }
        getObjets()
    }
}