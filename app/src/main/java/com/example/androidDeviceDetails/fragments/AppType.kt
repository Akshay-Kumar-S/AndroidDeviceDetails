package com.example.androidDeviceDetails.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
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


private var mContext: Context? = null

/**
 * A simple [Fragment] subclass.
 */
private const val TAG = "AppType"

class AppTypeUser(private var userApps: ArrayList<AppInfoCookedData>) : Fragment() {

    private lateinit var binding: FragmentAppTypeBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.root.post {
            Log.d(TAG, "onActivityCreated: ${userApps.size}")
            binding.appTypeListUser.adapter =
                AppInfoListAdapter(mContext!!, R.layout.appinfo_tile, userApps, null, true)
        }


        binding.appTypeListUser.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Utils.showAlertDialog(
                    mContext!!,
                    Utils.getApplicationIcon(userApps[position].packageName),
                    "test",
                    "testing"
                )
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_app_type,
            container,
            false
        )
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }


}

