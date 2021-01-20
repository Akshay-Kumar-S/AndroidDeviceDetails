package com.example.androidDeviceDetails.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermissionsListAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityPermissionsBinding

class PermissionsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPermissionsBinding
    private lateinit var controller: ActivityController<String>

    companion object {
        const val NAME = "permissions"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permissions)
        controller = ActivityController(
            NAME,
            binding,
            this,
            null,
            supportFragmentManager
        )
        binding.apply {
            permissionsListView.isEnabled = true
            permissionsListView.setOnItemClickListener { parent, _, position, _ ->
                showApps(parent, position)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
        }
    }

    private fun showApps(parent: AdapterView<*>, position: Int) {
        val adapter = parent.adapter as PermissionsListAdapter
        val item = adapter.getItem(position)
        val infoIntent = Intent(Settings.ACTION_PRIVACY_SETTINGS)
        intent.putExtra("permission", item)
//        infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
//        infoIntent.data = Uri.parse("package:${item?.get(position)}")
        startActivity(infoIntent)
    }

}