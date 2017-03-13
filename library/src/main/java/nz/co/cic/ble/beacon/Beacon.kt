package nz.co.cic.ble.beacon

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.beust.klaxon.JsonObject
import com.beust.klaxon.array
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by dipshit on 3/03/17.
 */

class Beacon(private val name: String, private val messages: HashMap<String, String>){

    private var uuid: UUID? = null
    private var hashedValues: HashMap<UUID, String>? = null

    init {

        this.initPrivate()

    }

    fun initPrivate() {
        this.uuid = UUID.nameUUIDFromBytes(name?.toByteArray())

        this.hashedValues = HashMap<UUID, String>()
        val it = messages?.entries?.iterator()
        while (it!!.hasNext()) {
            val pair = it.next()
            hashedValues?.put(UUID.nameUUIDFromBytes(pair.key.toString().toByteArray()), pair.value.toString())
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

            val it = hashedValues?.entries?.iterator()
            while (it!!.hasNext()) {
                val pair = it.next()
                val characteristic = BluetoothGattCharacteristic(pair.key, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ)
                characteristic.setValue(pair.value.toString().toByteArray())
                characteristics.add(characteristic)
            }

            return characteristics
        }

    fun toJSON(): JSONObject {
        var serviceJSON = JSONObject()
        serviceJSON.put("id", this.uuid.toString())

        var messageArray = JSONArray()
        compatMessages.forEach {
            message ->

            var messageObject = JSONObject()
            messageObject.put("id", message.uuid)
            messageObject.put("value", String(message.value))

            messageArray.put(messageObject)
        }
        serviceJSON.put("messages", messageArray)
        return serviceJSON
    }
}
