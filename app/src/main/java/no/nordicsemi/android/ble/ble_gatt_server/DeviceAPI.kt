package no.nordicsemi.android.ble.ble_gatt_server

import kotlinx.coroutines.channels.SendChannel


interface DeviceAPI {
	fun setForegroundServiceStatusChannel(sendChannel: SendChannel<String>)
}