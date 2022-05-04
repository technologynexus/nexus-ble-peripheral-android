package com.nexusgroup.ble.peripheral

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ble_gatt_server.DeviceAPI
import no.nordicsemi.android.ble.ble_gatt_server.GattService


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "activity"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private val defaultScope = CoroutineScope(Dispatchers.Default)
    private var gattServiceConn: GattServiceConn? = null
    private val foregroundServiceStatusChangeNotifications = Channel<String>()

    private fun log(priority: Int, message: String) {
        Log.println(priority, TAG, message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        defaultScope.launch {
            for (newValue in foregroundServiceStatusChangeNotifications) {
                log(Log.INFO, "foregroundServiceStatusChangeNotifications $newValue")
                runOnUiThread(Runnable {
                    val msg = findViewById<TextView>(R.id.statusText)
                    msg.text = newValue
                })
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
            binding?.setForegroundServiceStatusChannel(foregroundServiceStatusChangeNotifications)
        }
    }
}