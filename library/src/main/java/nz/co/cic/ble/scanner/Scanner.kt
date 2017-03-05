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
import io.reactivex.Observable

import org.jdeferred.Deferred
import org.jdeferred.FailCallback
import org.jdeferred.Promise
import org.jdeferred.impl.DeferredObject
import org.json.JSONObject

/**
 * Created by dipshit on 3/03/17.
 */

class Scanner(private val c: Context) {

    private var radio: Radio? = null
    private var connected: Boolean = false

    init {
        this.radio = Radio(c)
        this.radio!!.enable()
    }

    fun start(): Observable<JSONObject>{
        return this.radio!!.start()
    }

    fun stop() {
        if(this.radio != null){
            this.radio!!.stop()
        }
    }

}
