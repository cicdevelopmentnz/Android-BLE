package nz.co.cic.ble.beacon

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

import org.jdeferred.Deferred

/**
 * Created by dipshit on 3/03/17.
 */

class BeaconManager(private val c: Context) {

    private val manager: BluetoothManager
    private val adapter: BluetoothAdapter
    private var advertiser: BluetoothLeAdvertiser? = null

    private val backend: BeaconBackend

    private var advertiseCallback: AdvertiseCallback? = null

    init {

        this.manager = this.c.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        this.adapter = BluetoothAdapter.getDefaultAdapter()

        this.backend = BeaconBackend(this.c, manager)

    }

    fun addBeacon(b: Beacon) {
        this.backend.addBeacon(b)
    }

    fun removeBeacon(b: Beacon) {
        this.backend.removeBeacon(b)
    }

    fun start(): Flowable<Boolean>{
        if (this.advertiser == null) {
            this.advertiser = getAdvertiser()
            println("Supports advertising: " + this.adapter.isMultipleAdvertisementSupported)
        }

        return Flowable.create({
            subscriber ->
            this.advertiseCallback = object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                    super.onStartSuccess(settingsInEffect)
                    subscriber.onNext(true)
                }

                override fun onStartFailure(errorCode: Int) {
                    super.onStartFailure(errorCode)
                    println("Beacon failed to start reason: " + errorCode)
                    subscriber.onNext(false)
                }
            }

            this.advertiser!!.startAdvertising(voidSettings(), voidData(), this.advertiseCallback)
        }, BackpressureStrategy.BUFFER)
    }

    fun stop() {
        if (this.advertiser != null) {
            this.advertiser!!.stopAdvertising(advertiseCallback)
        }
    }

    private fun getAdvertiser(): BluetoothLeAdvertiser {
        return this.adapter.bluetoothLeAdvertiser
    }

    private fun voidSettings(): AdvertiseSettings {
        return AdvertiseSettings.Builder().setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER).setConnectable(true).build()
    }

    private fun voidData(): AdvertiseData {
        return AdvertiseData.Builder().setIncludeDeviceName(true).build()
    }

}
