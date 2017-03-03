package nz.co.cic.ble.beacon

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.Context

import java.util.ArrayList

/**
 * Created by dipshit on 3/03/17.
 */

class BeaconBackend(private val mContext: Context, bluetoothManager: BluetoothManager) : BluetoothGattServerCallback() {
/*    var server: BluetoothGattServer
    var beaconList: MutableList<Beacon>

    init {
        server = bluetoothManager.openGattServer(mContext, this)
        this.beaconList = ArrayList<Beacon>()
    }

    fun addBeacon(b: Beacon) {
        this.beaconList.add(b)
        this.server.addService(b.compatService)
    }

    fun removeBeacon(b: Beacon) {
        this.beaconList.remove(b)
        this.server.removeService(b.compatService)
    }

    override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
        super.onConnectionStateChange(device, status, newState)
    }

    override fun onServiceAdded(status: Int, service: BluetoothGattService) {
        super.onServiceAdded(status, service)
    }

    override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic) {
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
        server.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.value)
    }

    override fun onCharacteristicWriteRequest(device: BluetoothDevice, requestId: Int, characteristic: BluetoothGattCharacteristic, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray) {
        super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
    }

    override fun onDescriptorReadRequest(device: BluetoothDevice, requestId: Int, offset: Int, descriptor: BluetoothGattDescriptor) {
        super.onDescriptorReadRequest(device, requestId, offset, descriptor)
    }

    override fun onDescriptorWriteRequest(device: BluetoothDevice, requestId: Int, descriptor: BluetoothGattDescriptor, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray) {
        super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value)
    }

    override fun onExecuteWrite(device: BluetoothDevice, requestId: Int, execute: Boolean) {
        super.onExecuteWrite(device, requestId, execute)
    }

    override fun onNotificationSent(device: BluetoothDevice, status: Int) {
        super.onNotificationSent(device, status)
    }

    override fun onMtuChanged(device: BluetoothDevice, mtu: Int) {
        super.onMtuChanged(device, mtu)
    }
*/}
