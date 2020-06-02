package com.example.locationsystem.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.example.locationsystem.Global.DirectionInfo.EAST;
import static com.example.locationsystem.Global.DirectionInfo.NORTH;
import static com.example.locationsystem.Global.DirectionInfo.NORTHEAST;
import static com.example.locationsystem.Global.DirectionInfo.NORTHWEST;
import static com.example.locationsystem.Global.DirectionInfo.SOUTH;
import static com.example.locationsystem.Global.DirectionInfo.SOUTHEAST;
import static com.example.locationsystem.Global.DirectionInfo.SOUTHWEST;
import static com.example.locationsystem.Global.DirectionInfo.WEST;
import static com.example.locationsystem.Global.DirectionInfo.sourceX;
import static com.example.locationsystem.Global.DirectionInfo.sourceY;
import static com.example.locationsystem.Global.DirectionInfo.targetX;
import static com.example.locationsystem.Global.DirectionInfo.targetY;

public class BeaconUtils {

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    //利用txpower和rssi计算距离
    public  static double calculateAccuracy(int txPower,double rssi){
        if(rssi == 0){
            return -1.0;
        }
        double ratio = rssi * 1.0/ txPower;
        double result;
        if(ratio < 1.0){
            BigDecimal bigDecimal = new BigDecimal(Math.pow(ratio,10));
            result = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
            return result;
        }else{
            double accuracy = (0.89976) * Math.pow(ratio,7.7095) + 0.111;
            BigDecimal bigDecimal = new BigDecimal(accuracy);
            result= bigDecimal.setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
            return result;
        }
    }
    //三点定位计算手机坐标
    public static String confirmXY(ArrayList<Double> distances){
        double X=(distances.get(0)*distances.get(0)-distances.get(1)*distances.get(1)+100)/20;
        BigDecimal bigDecimalX = new BigDecimal(X);
        double accuracyX = bigDecimalX.setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
        double Y=(distances.get(0)*distances.get(0)-distances.get(2)*distances.get(2)+100)/20;
        BigDecimal bigDecimalY = new BigDecimal(Y);
        double accuracyY = bigDecimalY.setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
        sourceX = accuracyX;
        sourceY = accuracyY;
        return "("+accuracyX+","+accuracyY+")";
    }

    //通过坐标确定方向
    public static String confirmDirection(double x1, double y1, double x2, double y2){
        if(x1<x2){
            if(y1<y2){
               return NORTHEAST;
            }else if(y1==y2){
                return EAST;
            }else{
               return SOUTHEAST;
            }
        }else if(x1==x2){
            if(y1<y2){
                return SOUTH;
            }else if(y1==y2){
                return "两点重合";
            }else{
                return NORTH;
            }
        }else{
            if(y1<y2){
                return NORTHWEST;
            }else if(y1==y2){
               return WEST;
            }else{
               return SOUTHWEST;
            }
        }
    }

    //导航路线
    public static String navigationPath(String path){
        String ultimatePath;
        switch(path){

            case "A->B":
                ultimatePath= "请向"+confirmDirection(sourceX,sourceY,0,10)+"方向直行";
                break;
            case "A->C->B":
                ultimatePath=  "请向"+confirmDirection(sourceX,sourceY,0,0)+"方向直行,再向"+confirmDirection(0,0,0,10)+"直行";
                break;
            case "A->D->B":
                ultimatePath= "请向"+confirmDirection(sourceX,sourceY,10,0)+"方向直行，再向"+confirmDirection(10,0,0,10)+"直行";
                break;
            case "A->C":
                ultimatePath=  "请向"+confirmDirection(sourceX,sourceY,0,0)+"方向直行";
                break;
            case "A->B->C":
                ultimatePath= "请向"+confirmDirection(sourceX,sourceY,0,10)+"方向直行，再向"+confirmDirection(0,10,0,0)+"直行";
                break;
            case "A->D->C":
                ultimatePath= "请向"+confirmDirection(sourceX,sourceY,10,0)+"方向直行，再向"+confirmDirection(10,0,0,0)+"直行";
                break;
            case "A->D":
                ultimatePath= "请向"+confirmDirection(sourceX,sourceY,10,0)+"方向直行";
                break;
            case "A->B->C->D":
                ultimatePath= "请向"+confirmDirection(sourceX,sourceY,0,10)+"方向直行,再向"+confirmDirection(0,10,0,0)+"直行，再向"
                        +confirmDirection(0,0,0,10)+"方向直行";
                break;
            case "A->C->D":
                ultimatePath=  "请向"+confirmDirection(sourceX,sourceY,0,0)+"方向直行，再向"+confirmDirection(0,0,0,10)+"方向直行";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + path);
        }
        return ultimatePath;
    }
}
