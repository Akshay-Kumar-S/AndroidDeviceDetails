package com.example.androidDeviceDetails.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.databinding.ActivityBaseAppInfoBinding

class AppInfoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityBaseAppInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base_app_info)
        binding.apply {
            binding.appInfo.button.setOnClickListener(this@AppInfoActivity)
            binding.appInfoType.button.setOnClickListener(this@AppInfoActivity)
            binding.appPermissions.button.setOnClickListener(this@AppInfoActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.appInfo -> startActivity(Intent(this, AppEventActivity::class.java))
            R.id.appInfoType -> startActivity(Intent(this, AppTypeActivity::class.java))
            R.id.appPermissions -> startActivity(Intent(this, AppPermissionsActivity::class.java))
        }
    }
}