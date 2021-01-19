package com.example.androidDeviceDetails.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission)
//        binding.apply {
//            permisson.permissionTile.setOnClickListener(this@PermissionActivity)
//        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.permisson -> TODO("Not yet implemented")
        }
    }
}