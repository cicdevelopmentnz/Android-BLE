package nz.co.cic.ble.scanner

import android.bluetooth.BluetoothGattService

/**
 * Created by dipshit on 5/03/17.
 */

class RadioService(val service: BluetoothGattService){

    var serviceId: String? = null

    init {
        this.serviceId = service.uuid.toString()

    }
}
