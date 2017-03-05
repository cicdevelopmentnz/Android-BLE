package nz.co.cic.ble.scanner

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

/**
 * Created by dipshit on 5/03/17.
 */

class RadioServiceProcessor(val services: List<BluetoothGattService>){

    private var observableEmitter: ObservableEmitter<BluetoothGattService>? = null
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
            this.observableEmitter!!.onComplete()
        }
    }
}

class RadioCharacteristicProcessor(val service: BluetoothGattService){


    private var observableEmitter: ObservableEmitter<BluetoothGattCharacteristic>? = null
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
            this.observableEmitter!!.onComplete()
        }
    }
}
