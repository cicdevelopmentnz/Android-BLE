package nz.co.cic.ble.scanner

import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.reactivestreams.Subscriber
import java.util.jar.Manifest

/**
 * Created by dipshit on 4/03/17.
 */

class Radio(private val c: Context) : BluetoothGattCallback(){

    private val manager: BluetoothManager
    private val adapter: BluetoothAdapter

    private var scanner: BluetoothLeScanner? = null
    private var legacyCallback: BluetoothAdapter.LeScanCallback? = null
    private var scanCallback: ScanCallback? = null


    var isConnected: Boolean = false

    init {
        this.adapter = BluetoothAdapter.getDefaultAdapter()
        this.manager = c.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    }

    fun start() {
        this.scanDevices()!!.subscribe({
            btDevice ->

            var device = RadioDevice(c, btDevice)
            if(!isConnected){
                device.connect()!!.subscribe({
                    connectionStatus ->
                    isConnected = connectionStatus
                })
            }
        })
    }



    fun stop(){
        compatStop()
    }

    fun resolve(device: BluetoothDevice){
        var gatt = device.connectGatt(c, false, this)
    }

    fun enable(){
        if(!this.adapter.isEnabled){
            this.adapter.enable()
        }
    }

    fun scanDevices() : Observable<BluetoothDevice>?{
        return Observable.create<BluetoothDevice> {
            subscriber ->
            compatStart(subscriber)
        }
    }
    private fun compatStart(observable: ObservableEmitter<BluetoothDevice>){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(this.scanner == null){
                this.scanner = this.adapter.bluetoothLeScanner
            }
            this.scanCallback = object: ScanCallback(){
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    observable.onNext(result?.device)
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                    super.onBatchScanResults(results)
                    val it = results!!.iterator()
                    while(it.hasNext()){
                        var sr = it.next()
                        observable.onNext(sr.device)
                    }
                }
            }

            this.scanner!!.startScan(this.scanCallback)
        }else{
            this.legacyCallback = object: BluetoothAdapter.LeScanCallback {
                override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
                    observable.onNext(device)
                }
            }

            this.adapter.startLeScan(this.legacyCallback)
        }
    }

    private fun compatStop(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.scanner!!.stopScan(this.scanCallback)
        }else{
            this.adapter.stopLeScan(this.legacyCallback)
        }
    }


    //Bluetooth Gatt Callbacks
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int){
        super.onConnectionStateChange(gatt, status, newState)

    }
}
