package no.nordicsemi.android.ble.ble_gatt_server

import kotlinx.coroutines.channels.SendChannel


interface DeviceAPI {
	/**
	 * Change the value of the GATT characteristic that we're publishing
	 */
	fun setMyCharacteristicValue(value: ByteArray)

	fun setMyCharacteristicChangedChannel(sendChannel: SendChannel<ByteArray>)
}