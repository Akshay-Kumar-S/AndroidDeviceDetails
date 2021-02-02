package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.PhoneStateListener.LISTEN_NONE
import android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
import android.telephony.TelephonyManager
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.receivers.SignalChangeListener

/**
 *  Implements [BaseCollector].
 *  An event based collector which collects the CELLULAR signal data.
 *  Contains a [PhoneStateListener] : [SignalChangeListener] which is registered on
 *  initialization of this class.
 *  This listener requires [android.Manifest.permission.ACCESS_FINE_LOCATION] permission.
 **/
class SignalChangeCollector : BaseCollector() {
    private var mTelephonyManager: TelephonyManager =
        DeviceDetailsApplication.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    /**
     * Registers the [SignalChangeListener] with [LISTEN_SIGNAL_STRENGTHS].
     **/
    override fun start() {
        mTelephonyManager.listen(SignalChangeListener, LISTEN_SIGNAL_STRENGTHS)
    }

    /**
     * Unregisters the [SignalChangeListener] with [LISTEN_NONE].
     **/
    override fun stop() {
        mTelephonyManager.listen(SignalChangeListener, LISTEN_NONE)
    }


}