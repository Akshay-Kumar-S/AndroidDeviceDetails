package com.example.androidDeviceDetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class NavActivity : AppCompatActivity() {
    private var wifiFragment = WifiFragment()
    private var cellularFragment = CellularFragment()
    private lateinit var active: Fragment
    private var fragmentManager = supportFragmentManager
    lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        active=cellularFragment
        setContentView(R.layout.activity_nav)
        bottomNavigation = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.wifiStrength,
                R.id.cellularStrength
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigation.setupWithNavController(navController)

        fragmentManager.beginTransaction().add(R.id.fragment, wifiFragment, "2").hide(wifiFragment)
            .commit();
        fragmentManager.beginTransaction().add(R.id.fragment, cellularFragment, "1").commit();

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellularStrength -> {
                    fragmentManager.beginTransaction().hide(active).show(cellularFragment).commit()
                    active = cellularFragment
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifiStrength -> {
                    fragmentManager.beginTransaction().hide(active).show(wifiFragment).commit()
                    active = wifiFragment
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }
    }
}