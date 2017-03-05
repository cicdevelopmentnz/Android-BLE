package nz.co.cic.ble.scanner

import android.bluetooth.*
import android.content.Context

import rx.Observable
import rx.Subscriber

/**
 * Created by dipshit on 4/03/17.
 */

data class RadioDevice(private val mContext: Context, val device: BluetoothDevice) : BluetoothGattCallback(){

    var isConnected: Boolean? = false
    var isDiscovering: Boolean? = false

    private var gatt: BluetoothGatt? = null

    private var connectionObserver: Subscriber<in List<RadioService>>? = null
    private var statusObserver: Subscriber<in Boolean>? = null
    private var serviceObserver: Subscriber<in List<BluetoothGattService>>? = null
    private var characteristicObserver: Subscriber<in BluetoothGattCharacteristic>? = null

    init {

    }

    fun connect() : Observable<Boolean>?{
        println("CONNECT")
        return Observable.create {
            subscriber ->
            this.statusObserver = subscriber
            gatt = device.connectGatt(mContext, false, this)
        }
    }

    fun discover(): Observable<List<RadioService>>?{
        return Observable.create {
            subscriber ->
            this.connectionObserver = subscriber
        }
    }

    fun disconnect(){
        println("DISCONNECT")
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
        this.statusObserver!!.onNext(parseState(newState))

        if(this.isConnected!!) {
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
                        characteristicObserver!!.onCompleted()
                        radioServices.add(radioService)
                        serviceProcessor.next()
                    })
                }, {
                    err ->

                }, {
                    this.connectionObserver!!.onNext(radioServices.toList())
                    this.connectionObserver!!.onCompleted()
                })

            })
        }

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
        this.serviceObserver!!.onCompleted()
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        this.characteristicObserver!!.onNext(characteristic)
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        super.onReadRemoteRssi(gatt, rssi, status)
    }

}
