package nz.co.cic.ble.scanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

/**
 * Created by dipshit on 4/03/17.
 */

class Radio(private val c: Context){

    private val manager: BluetoothManager
    private val adapter: BluetoothAdapter

    private var scanner: BluetoothLeScanner? = null
    private var legacyCallback: BluetoothAdapter.LeScanCallback? = null
    private var scanCallback: ScanCallback? = null

    init {
        this.adapter = BluetoothAdapter.getDefaultAdapter()
        this.manager = c.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    fun start(): Observable<BluetoothDevice>? {
        return Observable.create<BluetoothDevice> { subscriber ->
            compatStart(subscriber);
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

    fun stop(){
        compatStop()
    }
}
