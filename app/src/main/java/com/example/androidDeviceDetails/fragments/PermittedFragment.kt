package com.example.androidDeviceDetails.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermittedAppsAdapter
import com.example.androidDeviceDetails.databinding.AppTypeMoreInfoBinding
import com.example.androidDeviceDetails.databinding.FragmentPermittedBinding
import com.example.androidDeviceDetails.models.appInfo.AppTypeModel
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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
                showAlertDialog(
                    mContext!!, getDetails(mContext!!, userApps[position])
                )
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

    private fun showAlertDialog(context: Context, appTypeModel: AppTypeModel) {
        val binding: AppTypeMoreInfoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.app_type_more_info,
            null,
            false
        )
        binding.Icon.setImageDrawable(appTypeModel.appIcon)
        binding.appTitle.text = appTypeModel.appTitle
        binding.packageName.text = appTypeModel.packageName
        binding.versionCode.text = appTypeModel.versionCode
        binding.versionName.text = appTypeModel.versionName
        binding.appSize.text = appTypeModel.packageSize
        binding.installTime.text = appTypeModel.installTime
        binding.updateTime.text = appTypeModel.updateTime
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setView(binding.root)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getDetails(
        context: Context,
        appInfoCookedData: PermittedAppsCookedData
    ): AppTypeModel {
        val simpleDateFormat = SimpleDateFormat("EEE, MMM d ''yy, hh:mm a", Locale.ENGLISH)
        val packageInfo = context.packageManager.getPackageInfo(appInfoCookedData.packageName, 0)
        val pInfo = context.packageManager.getApplicationInfo(appInfoCookedData.packageName, 0)
        val file = File(pInfo.sourceDir)
        var versionCode: Long = 0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            versionCode = packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode = packageInfo.versionCode.toLong()
        }

        return AppTypeModel(
            Utils.getApplicationIcon(appInfoCookedData.packageName),
            appInfoCookedData.apkTitle,
            appInfoCookedData.packageName,
            versionCode.toString(),
            packageInfo.versionName.toString(),
            Utils.getFileSize(file.length()),
            simpleDateFormat.format(Date(packageInfo.firstInstallTime)),
            simpleDateFormat.format(Date(packageInfo.lastUpdateTime))
        )
    }
}