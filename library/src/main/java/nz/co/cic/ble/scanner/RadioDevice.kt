package nz.co.cic.ble.scanner

import android.bluetooth.*
import android.content.Context
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.reactivestreams.Subscriber

/**
 * Created by dipshit on 4/03/17.
 */

data class RadioDevice(private val mContext: Context, private val device: BluetoothDevice) : BluetoothGattCallback(){

    var isConnected: Boolean? = false
    var isDiscovering: Boolean? = false

    private var connectionObserver: ObservableEmitter<Boolean>? = null
    private var serviceObserver: ObservableEmitter<List<BluetoothGattService>>? = null
    private var characteristicObserver: ObservableEmitter<BluetoothGattCharacteristic>? = null

    init {

    }

    fun connect() : Observable<Boolean>? {
        println("Connect cmd")
        return Observable.create {
            subscriber ->
            this.connectionObserver = subscriber
            device.connectGatt(mContext, false, this)
        }
    }

    fun disconnect(){

    }

    private fun discoverServices(gatt: BluetoothGatt): Observable<List<BluetoothGattService>>?{
        if(this.isConnected!! && !this.isDiscovering!!) {
            isDiscovering = true
            return Observable.create {
                subscriber ->
                this.serviceObserver = subscriber
                this.isDiscovering = gatt.discoverServices()
            }
        }
        return null
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)

        this.isConnected = parseState(newState)
        this.connectionObserver!!.onNext(parseState(newState))

        discoverServices(gatt!!)!!.subscribe({
            service ->

            var serviceProcessor = RadioServiceProcessor(service)
            serviceProcessor.queue().subscribe({
                processService ->
                println("Processing: " + processService.uuid)

                var characteristicProcessor = RadioCharacteristicProcessor(processService)

                startReadingCharacteristics().subscribe({
                    characteristic ->
                    println("Resolved char: " + String(characteristic.value))
                    characteristicProcessor.next()
                })

                characteristicProcessor.queue().subscribe({
                    char ->
                    readCharacteristic(gatt, char)
                }, {

                }, {
                    println("Next service")
                    serviceProcessor.next()
                })

            }, {

            }, {
                println("Processed all services")
            })

        })

    }

    private fun startReadingCharacteristics(): Observable<BluetoothGattCharacteristic> {
        return Observable.create {
            subscriber ->
            this.characteristicObserver = subscriber
        }
    }

    private fun readCharacteristic(gatt: BluetoothGatt, char: BluetoothGattCharacteristic){
        println("Processing char: " + char.uuid)
        gatt.readCharacteristic(char)
    }

    private fun parseState(state: Int): Boolean{
        if(state == BluetoothGatt.STATE_CONNECTED){
            return true;
        }else if(state == BluetoothGatt.STATE_DISCONNECTED){
            return false;
        }
        return false;
    }


    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)

        this.serviceObserver!!.onNext(gatt!!.services)
        this.serviceObserver!!.onComplete()
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        println("Char read: " + String(characteristic!!.value))
        this.characteristicObserver!!.onNext(characteristic)
        this.characteristicObserver!!.onComplete()
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        super.onReadRemoteRssi(gatt, rssi, status)
    }

}
