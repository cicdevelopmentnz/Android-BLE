package nz.co.cic.ble.scanner

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import org.json.JSONArray
import org.json.JSONObject

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

    fun toJSON(): JSONObject{
        var serviceObject =JSONObject()
        serviceObject.put("serviceId", serviceId)

        var messageObject = JSONArray()
        messages!!.forEach {
            it ->
            messageObject.put(it.toJSON())
        }
        serviceObject.put("messages", messageObject)

        return serviceObject
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

    fun toJSON(): JSONObject{
        var messageObject = JSONObject()
        messageObject.put("id", messageId)
        messageObject.put("val", message)
        return messageObject
    }
}
