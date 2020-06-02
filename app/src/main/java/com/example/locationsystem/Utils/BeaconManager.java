package com.example.locationsystem.Utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.example.locationsystem.Entity.BeaconClass;

public class BeaconManager {
    BluetoothAdapter bluetoothAdapter;

    private BeaconListener beaconListener;

    public void setBeaconListener(BeaconListener beaconListener){
        this.beaconListener=beaconListener;
    }

    public BeaconManager(Context context){
        BluetoothManager bluetoothManager=(BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter=bluetoothManager.getAdapter();
    }
    public void startScanBeacon(){
        bluetoothAdapter.stopLeScan(leScanCallback);
        bluetoothAdapter.startLeScan(leScanCallback);
    }

    public void stopScan(){
        bluetoothAdapter.stopLeScan(leScanCallback);
    }
    private BluetoothAdapter.LeScanCallback leScanCallback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothdeivce, int rssi, byte[] scandata) {
            BeaconClass.Beacon beacon=BeaconClass.fromScanData(scandata, bluetoothdeivce, rssi);
            if(beacon==null){
            }else{
                if(beaconListener!=null){
                    beaconListener.onScanBeacon(beacon);
                }
            }
        }
    };

    public interface BeaconListener{
        public void onScanBeacon(BeaconClass.Beacon beacon);
    }
}
