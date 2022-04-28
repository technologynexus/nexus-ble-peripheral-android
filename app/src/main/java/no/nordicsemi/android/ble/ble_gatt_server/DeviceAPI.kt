package no.nordicsemi.android.ble.ble_gatt_server

import kotlinx.coroutines.channels.SendChannel


interface DeviceAPI {
	/**
	 * Change the value of the GATT characteristic that we're publishing
	 */
	fun setMyCharacteristicValue(value: String)

	fun setMyCharacteristicChangedChannel(sendChannel: SendChannel<String>)
}