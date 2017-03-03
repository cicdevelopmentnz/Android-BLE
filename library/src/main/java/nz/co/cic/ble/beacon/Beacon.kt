package nz.co.cic.ble.beacon

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import java.util.ArrayList
import java.util.HashMap
import java.util.UUID

/**
 * Created by dipshit on 3/03/17.
 */

class Beacon(var name: String, var messages: HashMap<String, String>) {

  /*  private var uuid: UUID? = null
    private var hashedValues: HashMap<UUID, String>? = null

    init {

        this.initPrivate()

    }

    fun initPrivate() {
        this.uuid = UUID.fromString(name)

        this.hashedValues = HashMap<UUID, String>()
        val it = messages.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Entry<*, *>
            hashedValues!!.put(UUID.fromString(pair.key.toString()), pair.value.toString())
        }
    }


    val compatService: BluetoothGattService
        get() {
            val service = BluetoothGattService(this.uuid, BluetoothGattService.SERVICE_TYPE_PRIMARY)
            val characteristics = compatMessages
            val it = characteristics.iterator()
            while (it.hasNext()) {
                service.addCharacteristic(it.next())
            }

            return service
        }

    val compatMessages: List<BluetoothGattCharacteristic>
        get() {
            val characteristics = ArrayList<BluetoothGattCharacteristic>()

            val it = hashedValues!!.entries.iterator()
            while (it.hasNext()) {
                val pair = it.next() as Entry<*, *>
                val characteristic = BluetoothGattCharacteristic(UUID.fromString(pair.key.toString()), BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ)
                characteristic.setValue(pair.value.toString().toByteArray())
                characteristics.add(characteristic)
            }

            return characteristics
        }
*/

}
