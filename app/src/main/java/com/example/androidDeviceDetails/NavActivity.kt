package com.example.androidDeviceDetails

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.androidDeviceDetails.models.RoomDB
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class NavActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {
    private var db = RoomDB.getDatabase()!!
    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0
    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0
    var toggle=0
    lateinit var  button:Button
    private var wifiFragment = WifiFragment()
    private var cellularFragment = CellularFragment()
    private lateinit var active: Fragment
    private var fragmentManager = supportFragmentManager
    lateinit var bottomNavigation: BottomNavigationView
    lateinit var imageFrom:ImageView
    lateinit var imageTo:ImageView
    lateinit var txtFrom:TextView
    lateinit var txtTo:TextView
    var fromTimestamp:Long=0
    var toTimestamp:Long=0

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

        pickDate()
        button=findViewById(R.id.button2)
        button.setOnClickListener(){
           wifiFragment.setTextViewText("working")
        }
    }
    private fun getDateTimeCalender() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }
    private fun pickDate() {
        val txt: TextView
        imageFrom = findViewById(R.id.imageView2)
        imageFrom.setOnClickListener {
            toggle=1
            getDateTimeCalender()
            DatePickerDialog(this, this, year, month, day).show()
        }
        imageTo = findViewById(R.id.imageView)
        imageTo.setOnClickListener {
            toggle=2
            getDateTimeCalender()
            DatePickerDialog(this, this, year, month, day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        getDateTimeCalender()
        TimePickerDialog(this, this, hour, minute, true).show()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        txtFrom=findViewById(R.id.textView3)
        txtTo=findViewById(R.id.textView4)
        savedHour = hourOfDay
        savedMinute = minute
        Log.d("calender", "$savedDay,$savedMonth,$savedYear")
        Log.d("calender", "$savedHour,$savedMinute")
        if(toggle==1){fromTimestamp=getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour, savedMinute)
        txtFrom.text="$savedDay/${savedMonth+1}/$savedYear  $savedHour:$savedMinute"}
        if(toggle==2){toTimestamp=getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour, savedMinute)
            txtTo.text="$savedDay/${savedMonth+1}/$savedYear  $savedHour:$savedMinute"}
//        GlobalScope.launch {
//            val wifiList=  db.wifiDao().getAllBetween(
//                getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour, savedMinute),
//                getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour + 1, savedMinute)
//            )
//            val cellularList=db.cellularDao().getAllBetween(
//                getTimeStamp(
//                    savedDay,
//                    savedMonth + 1,
//                    savedYear,
//                    savedHour,
//                    savedMinute
//                ),
//                getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour + 1, savedMinute)
//            )
//            Log.d(
//                "calenderdata",
//                cellularList.toString()
//            )
//            Log.d(
//                "calenderdata1",
//                wifiList.toString()
//            )
//            runOnUiThread {
//                val text:TextView=findViewById(R.id.textView3)
//                text.text=cellularList.toString()
//            }
//        }
    }
    fun getTimeStamp(day: Int, month: Int, year: Int, hour: Int, minute: Int): Long {
        val hexString = Integer.toHexString(hour * 60 * 60 + minute * 60)
        val str_date = "$day-$month-$year"
        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = formatter.parse(str_date) as Date
        var timestamp = date.time
        //Log.d("calender", "${hexString.toLong(16)}")
        timestamp = timestamp / 1000 + hexString.toLong(16)
        Log.d("calender", "${timestamp * 1000}")
        return timestamp*1000
    }
}