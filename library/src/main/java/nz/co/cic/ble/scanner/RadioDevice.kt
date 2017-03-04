package nz.co.cic.ble.scanner

import android.bluetooth.*
import android.content.Context
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

/**
 * Created by dipshit on 4/03/17.
 */

data class RadioDevice(private val mContext: Context, private val device: BluetoothDevice) : BluetoothGattCallback(){

    var isConnected: Boolean = false
    var isDiscovering: Boolean = false

    var gatt: BluetoothGatt? = null


    private var serviceObserver : ObservableEmitter<BluetoothGattService>? = null
    private var connectionObserver : ObservableEmitter<Boolean>? = null

    init {

    }

    fun connect() : Observable<Boolean>? {
        return Observable.create<Boolean> {
            subscriber ->
            this.connectionObserver = subscriber
            gatt = device.connectGatt(mContext, false, this)
        }
    }

    fun disconnect(){
        this.gatt?.disconnect()
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)

        if(newState == BluetoothGatt.STATE_CONNECTED){
            isConnected = true
            this.connectionObserver!!.onNext(true)
        }else if(newState == BluetoothGatt.STATE_DISCONNECTED){
            isConnected = false
            this.connectionObserver!!.onNext(false)
        }

        if(isConnected && !isDiscovering){
            isDiscovering = true
            discoverServices().subscribe({
                service ->
                println("Found a service: " + service.uuid)
            })

            gatt!!.readRemoteRssi()
        }

    }

    private fun discoverServices() : Observable<BluetoothGattService>{
            return Observable.create<BluetoothGattService> {
                subscriber ->
                    this.serviceObserver = subscriber
                    gatt!!.discoverServices()
            }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)
        var it = gatt!!.services.iterator()
        while(it.hasNext()){
            var service : BluetoothGattService = it!!.next() as BluetoothGattService
            this.serviceObserver?.onNext(service)
        }

        this.serviceObserver?.onComplete()
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        super.onReadRemoteRssi(gatt, rssi, status)
        println("RSSI Update: " + rssi)
    }

    fun equals(device: RadioDevice): Boolean{
        if(device.device.address.equals(this.device.address)){
            return true;
        }else{
            return false;
        }
    }
}
