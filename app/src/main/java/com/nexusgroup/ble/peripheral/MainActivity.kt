package com.nexusgroup.ble.peripheral

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.nexusgroup.personal.sdk.android.ble.BLEDeviceSession
import com.nexusgroup.personal.sdk.android.ble.SDKHex
import no.nordicsemi.android.ble.ble_gatt_server.DeviceAPI
import no.nordicsemi.android.ble.ble_gatt_server.GattService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "activity"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val defaultScope = CoroutineScope(Dispatchers.Default)
    private var gattServiceConn: GattServiceConn? = null
    private val myCharacteristicValueChangeNotifications = Channel<ByteArray>()
    private var session: BLEDeviceSession? = null

    private fun log(priority: Int, message: String) {
        Log.println(priority, TAG, message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        session = BLEDeviceSession(this)

        defaultScope.launch {
            for (newValue in myCharacteristicValueChangeNotifications) {
                mainHandler.run {
                    log(Log.INFO, "myCharacteristicValueChangeNotifications ${SDKHex.encode(newValue)}")
                    var reply = session?.handleData(newValue)
                    if (reply != null) {
                        gattServiceConn?.binding?.setMyCharacteristicValue(reply)
                    }
                }
            }
        }

        // Startup our Bluetooth GATT service explicitly so it continues to run even if
        // this activity is not in focus
        startForegroundService(Intent(this, GattService::class.java))
    }

    override fun onStart() {
        super.onStart()

        val latestGattServiceConn = GattServiceConn()
        if (bindService(Intent(this, GattService::class.java), latestGattServiceConn, 0)) {
            gattServiceConn = latestGattServiceConn
        }
    }

    override fun onStop() {
        super.onStop()

        if (gattServiceConn != null) {
            unbindService(gattServiceConn!!)
            gattServiceConn = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // We only want the service around for as long as our app is being run on the device
        stopService(Intent(this, GattService::class.java))
    }

    private inner class GattServiceConn : ServiceConnection {
        var binding: DeviceAPI? = null

        override fun onServiceDisconnected(name: ComponentName?) {
            binding = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binding = service as? DeviceAPI
            binding?.setMyCharacteristicChangedChannel(myCharacteristicValueChangeNotifications)
        }
    }
}