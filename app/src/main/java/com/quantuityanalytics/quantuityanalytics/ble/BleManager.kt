package com.quantuityanalytics.quantuityanalytics.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat

@SuppressLint("MissingPermission")
class BleManager(val context: Context, val deviceAdapter: BleDeviceAdapter) {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var scanning = false
    private val handler = Handler()
    private var bluetoothManager: BluetoothManager? = null

    companion object {
        const val TAG: String = "QuantuityAnalytics.TestActivity"
        const val SCANNING_PERIOD: Int = 10000
    }

    fun startScanning() {
        Log.d(TAG, "Start scanning...")
        deviceAdapter.cleanDeviceList()
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager!!.adapter
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        //val scanFilters = listOf<ScanFilter>()
        //val scanSettings = ScanSettings.Builder().build()

        Log.d(TAG, "Scanning...")
        /*if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "NO PERMISSION")
            return
        }*/

        // Stops scanning after a pre-defined scan period.
        if (!scanning) {
            handler.postDelayed({
                scanning = false
                scanner?.stopScan(leScanCallback)
            }, SCANNING_PERIOD.toLong())
            scanning = true
            scanner?.startScan(leScanCallback)
        } else {
            scanning = false
            scanner?.stopScan(leScanCallback)
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "No permissions")
            return
        }
        Log.d(TAG, "Connecting to device ${device.address}")
        device.connectGatt(context, false, gattCallback)
    }

    private val leScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d(TAG, "leScanCallback")
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->
                /*if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d(TAG, "No permissions")
                    return
                }*/

                Log.d(TAG, "Device found: ${device.name} - ${device.address}")

                if (!deviceAdapter.containDevice(device)) {
                    Log.d(TAG, "Adding a new device")
                    deviceAdapter.addDevice(device)
                    deviceAdapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Device already saved: ${device.address}")
                }

                //device.connectGatt(this@MainActivity, false, gattCallback)
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected to GATT server.")
                    /*if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Log.d(TAG, "No permissions")
                        return
                    }*/
                    gatt?.discoverServices()
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from GATT server.")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt?.services?.forEach { service ->
                    Log.d(TAG, "Service discovered: ${service.uuid}")
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristic?.value?.let { value ->
                    Log.d(TAG, "Characteristic read: ${value.joinToString()}")
                }
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic written successfully")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            characteristic?.value?.let { value ->
                Log.d(TAG, "Characteristic changed: ${value.joinToString()}")
            }
        }
    }
}