package nz.co.cic.ble.scanner

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.subscribers.ResourceSubscriber
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by dipshit on 5/03/17.
 */

class RadioDeviceProcessor(private val mContext: Context, private val emitter: FlowableEmitter<JSONObject>): ResourceSubscriber<BluetoothDevice>() {
    override fun onStart() {
        request(1)
    }

    override fun onComplete() {
    }

    override fun onError(t: Throwable?) {
    }

    override fun onNext(t: BluetoothDevice?) {
        this.processDevice(t)
    }

    private fun processDevice(btDevice: BluetoothDevice?){
        var device = RadioDevice(mContext, btDevice!!)
        device.discover()!!.subscribe({
            services ->

            emitter.onNext(joinToJSON(device, services))
            device.disconnect()
        })

        device.connect()!!.subscribe({
            state ->

            if(!state){
                println("Disconnected")
                request(1)
            }else{
                println("Connected")
            }
        })
    }

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


}

class RadioServiceProcessor(val services: List<BluetoothGattService>){

    private var observableEmitter: FlowableEmitter<BluetoothGattService>? = null
    private var ix: Int = -1

    fun queue(): Flowable<BluetoothGattService>{
        return Flowable.create({
            subscriber ->
            this.observableEmitter = subscriber
            next()
        }, BackpressureStrategy.BUFFER)
    }

    fun next(){
        ix += 1
        if(ix < services.size){
            this.observableEmitter!!.onNext(services.get(ix))
        }else{
            this.observableEmitter!!.onComplete()
        }
    }
}

class RadioCharacteristicProcessor(val service: BluetoothGattService){


    private var observableEmitter: FlowableEmitter<BluetoothGattCharacteristic>? = null
    private var ix : Int = -1

    init {

    }

    fun queue() : Flowable<BluetoothGattCharacteristic>{
        return Flowable.create({
            subscriber ->
            this.observableEmitter = subscriber
            next()
        }, BackpressureStrategy.BUFFER)
    }

    fun next(){
        ix += 1
        if(ix < service.characteristics.size) {
            this.observableEmitter!!.onNext(service.characteristics.get(ix))
        }else{
            this.observableEmitter!!.onComplete()
        }
    }
}
