package nz.co.cic.ble.scanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.widget.Toast

import org.jdeferred.Deferred
import org.jdeferred.FailCallback
import org.jdeferred.Promise
import org.jdeferred.impl.DeferredObject

/**
 * Created by dipshit on 3/03/17.
 */

class Scanner(private val c: Context) {

    private var radio: Radio? = null

    init {
        this.radio = Radio(c)
        this.radio!!.enable()
    }


    fun start() {

        this.radio!!.start()!!.subscribe({
            device ->

            println("We have a new device: " + device.address)
        })
    }



    fun stop() {
        if(this.radio != null){
            this.radio!!.stop()
        }
    }

}
