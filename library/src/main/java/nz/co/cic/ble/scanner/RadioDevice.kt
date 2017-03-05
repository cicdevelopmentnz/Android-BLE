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

    private var gatt: BluetoothGatt? = null

    private var connectionObserver: ObservableEmitter<List<RadioService>>? = null
    private var serviceObserver: ObservableEmitter<List<BluetoothGattService>>? = null
    private var characteristicObserver: ObservableEmitter<BluetoothGattCharacteristic>? = null

    init {

    }

    fun connect() : Observable<List<RadioService>>? {
        return Observable.create {
            subscriber ->
            this.connectionObserver = subscriber

            gatt = device.connectGatt(mContext, false, this)
        }
    }

    fun disconnect(){
        gatt!!.disconnect()
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

        discoverServices(gatt!!)!!.subscribe({
            services ->

            var serviceProcessor = RadioServiceProcessor(services)
            var radioServices = mutableListOf<RadioService>()

            serviceProcessor.queue().subscribe({
                processService ->

                var radioService = RadioService(processService)

                var characteristicProcessor = RadioCharacteristicProcessor(processService)

                startReadingCharacteristics().subscribe({
                    characteristic ->

                    radioService.addMessage(characteristic)

                    characteristicProcessor.next()
                })

                characteristicProcessor.queue().subscribe({
                    char ->
                    readCharacteristic(gatt, char)
                }, {

                }, {
                    characteristicObserver!!.onComplete()
                    radioServices.add(radioService)
                    serviceProcessor.next()
                })
            }, {
                err ->

            }, {
                this.connectionObserver!!.onNext(radioServices.toList())
                this.connectionObserver!!.onComplete()
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
        if(char.properties.and(BluetoothGattCharacteristic.PROPERTY_READ) == BluetoothGattCharacteristic.PROPERTY_READ){
            gatt.readCharacteristic(char)
        }else{
            characteristicObserver!!.onNext(char)
        }
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
        this.characteristicObserver!!.onNext(characteristic)
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        super.onReadRemoteRssi(gatt, rssi, status)
    }

}
