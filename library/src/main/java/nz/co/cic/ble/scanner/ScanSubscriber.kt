package nz.co.cic.ble.scanner

import android.bluetooth.BluetoothDevice
import android.content.Context

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

/**
 * Created by dipshit on 5/03/17.
 */

class ScanSubscriber(private val c: Context) : Subscriber<BluetoothDevice> {

    private var subscription: Subscription? = null

    override fun onSubscribe(s: Subscription) {
        this.subscription = s
    }

    override fun onNext(device: BluetoothDevice) {
        this.process(device)
    }

    override fun onError(t: Throwable) {

    }

    override fun onComplete() {

    }

    private fun process(dev: BluetoothDevice){
        var device = RadioDevice(c, dev)
        device.connect()
    }
}
