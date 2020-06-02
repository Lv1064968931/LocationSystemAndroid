package com.example.locationsystem.Entity;

import android.bluetooth.BluetoothDevice;

import static com.example.locationsystem.Utils.BeaconUtils.bytesToHexString;
import static com.example.locationsystem.Utils.BeaconUtils.calculateAccuracy;

public class BeaconClass {
    static public class Beacon{
        public String beaconName;
        public int major;
        public int minor;
        public String uuid;
        public String bluetoothAddress;
        public int txPower;
        public int rssi;
        public double distance;
        public double x;
        public double y;
        public String location;

        public String toString(){
            return  "beaconName"+beaconName+"uuid"+uuid+"major"+major;
        }
    }

    public static Beacon fromScanData(byte[] scanData, BluetoothDevice device, int rssi){
        int startByte = 1;
        boolean patternFound = false;
        //寻找ibeacon
        while (startByte <= 5){
            if((scanData[startByte + 2] & 0xff)==0x02 && (scanData[startByte + 3] & 0xff)==0x15){
                patternFound = true;
                break;
            }
            startByte++;
        }
        if(patternFound){
            int major = (scanData[startByte+20] & 0xff) * 0x100 + (scanData[startByte+21] & 0xff);
            int minor = (scanData[startByte+22] & 0xff) * 0x100 + (scanData[startByte+23] & 0xff);
            int txPower = (int)scanData[startByte+24]; // this one is signed
            double distance = calculateAccuracy(txPower,rssi);

            byte[] proximityUuidBytes = new byte[16];
            System.arraycopy(scanData, startByte+4, proximityUuidBytes, 0, 16);
            String hexString = bytesToHexString(proximityUuidBytes);
            StringBuilder sb = new StringBuilder();
            sb.append(hexString.substring(0,8));
            sb.append("-");
            sb.append(hexString.substring(8,12));
            sb.append("-");
            sb.append(hexString.substring(12,16));
            sb.append("-");
            sb.append(hexString.substring(16,20));
            sb.append("-");
            sb.append(hexString.substring(20,32));
            String uuid = sb.toString();

            Beacon beacon=new Beacon();
            beacon.beaconName=device.getName();
            beacon.major=major;
            beacon.minor=minor;
            beacon.uuid=uuid;
            beacon.bluetoothAddress=device.getAddress();
            beacon.txPower=txPower;
            beacon.rssi=rssi;
            beacon.distance=distance;


            return beacon;
        }else {
            return null;
        }
    }
}
