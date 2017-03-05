package nz.co.cic.ble.scanner

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService

/**
 * Created by dipshit on 5/03/17.
 */

data class RadioService(val service: BluetoothGattService){

    var serviceId: String? = null
    var messages: MutableList<RadioMessage>? = null

    init {
        this.serviceId = service.uuid.toString()
        this.messages = mutableListOf()
    }

    fun addMessage(char: BluetoothGattCharacteristic){
        this.messages!!.add(RadioMessage(char))
    }
}

data class RadioMessage(val characteristic: BluetoothGattCharacteristic){

    var messageId: String? = null
    var message: String? = null

    init{
        this.messageId = characteristic.uuid.toString()
        if(characteristic.value != null) {
            this.message = String(characteristic.value)
        }
    }
}
