package nz.co.cic.ble.scanner

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import rx.Observable
import rx.Subscriber

/**
 * Created by dipshit on 5/03/17.
 */

class RadioDeviceProcessor(private val mContext: Context, private val dataSubscriber: Subscriber<in JSONObject>): Subscriber<BluetoothDevice>() {

    private fun joinToJSON(device: RadioDevice, services : List<RadioService> ) : JSONObject{
        var obj = JSONObject()
        obj.put("deviceAddress", device.device.address)

        var messageArr = JSONArray()
        services.forEach {
            service ->
            messageArr.put(service.toJSON())
        }

        obj.put("messages", messageArr)
        return obj
    }

    override fun onStart() {
        request(1)
    }

    override fun onNext(p0: BluetoothDevice?) {
        var device = RadioDevice(mContext, p0!!)
        device.connect()!!.subscribe({
            services ->

            device.disconnect()
            dataSubscriber.onNext(joinToJSON(device, services))

            request(1)
        })

    }

    override fun onError(p0: Throwable?) {
        p0!!.printStackTrace()
    }

    override fun onCompleted() {

    }

}

class RadioServiceProcessor(val services: List<BluetoothGattService>){

    private var observableEmitter: Subscriber<in BluetoothGattService>? = null
    private var ix: Int = -1

    fun queue(): Observable<BluetoothGattService>{
        return Observable.create {
            subscriber ->
            this.observableEmitter = subscriber
            next()
        }
    }

    fun next(){
        ix += 1
        if(ix < services.size){
            this.observableEmitter!!.onNext(services.get(ix))
        }else{
            this.observableEmitter!!.onCompleted()
        }
    }
}

class RadioCharacteristicProcessor(val service: BluetoothGattService){


    private var observableEmitter: Subscriber<in BluetoothGattCharacteristic>? = null
    private var ix : Int = -1

    init {

    }

    fun queue() : Observable<BluetoothGattCharacteristic>{
        return Observable.create {
            subscriber ->
            this.observableEmitter = subscriber
            next()
        }
    }

    fun next(){
        ix += 1
        if(ix < service.characteristics.size) {
            this.observableEmitter!!.onNext(service.characteristics.get(ix))
        }else{
            this.observableEmitter!!.onCompleted()
        }
    }
}
