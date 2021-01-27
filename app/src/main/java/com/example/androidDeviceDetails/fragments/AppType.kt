package com.example.androidDeviceDetails.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.AppInfoListAdapter
import com.example.androidDeviceDetails.databinding.FragmentAppTypeBinding
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData
import com.example.androidDeviceDetails.utils.Utils

/**
 * A simple [Fragment] subclass.
 */

class AppTypeUser(private var userApps: ArrayList<AppInfoCookedData>) : Fragment() {

    private lateinit var binding: FragmentAppTypeBinding
    private var mContext: Context? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.root.post {
            binding.appTypeListUser.adapter =
                AppInfoListAdapter(mContext!!, R.layout.appinfo_tile, userApps, null, true)
        }
        binding.appTypeListUser.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val packageDetails = Utils.getPackageDetails(mContext!!, userApps[position])
                Utils.showAlertDialog(mContext!!, packageDetails)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_type, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}