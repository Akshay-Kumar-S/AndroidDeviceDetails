package com.example.androidDeviceDetails.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermittedAppsAdapter
import com.example.androidDeviceDetails.databinding.FragmentPermittedBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData

class PermittedFragment(private var userApps: List<PermittedAppsCookedData>) : Fragment() {

    private lateinit var binding: FragmentPermittedBinding
    private var mContext: Context? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.root.post {
            binding.appList.adapter =
                PermittedAppsAdapter(mContext!!, R.layout.permitted_app_info_tile, userApps)
        }
        binding.appList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                showApps(parent,position)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_permitted, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

        private fun showApps(parent: AdapterView<*>, position: Int) {
        val adapter = parent.adapter as PermittedAppsAdapter
        val item = adapter.getItem(position)
        val infoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
        infoIntent.data = Uri.parse("package:${item?.package_name}")
        startActivity(infoIntent)
    }
}