package com.example.locationsystem.Utils;

public class LocateUtil {
    //三点定位算法确定坐标
    public static String confirmXY(double x1,double y1,double x2,double y2,double x3,double y3){
        double X,Y,Z,lastX,lastY;
        X=(y2-y1)/(x2-x1);
        Y= y1-X*x1;
        double xMiddle = (x1+x2)/2;
        double yMiddle = (y1+y2)/2;
        if(X != 0){
            Z = yMiddle -(-1/X)*xMiddle;
            lastX = (Math.pow(x1,2)+Math.pow(y1,2)-Math.pow(x3,2)-Math.pow(y3,2)-2*Z*y1+2*Z*y3)/(2*((x1-x3)-(1/X)*(y1-y3)));
            lastY = (-1/X)*lastX+Z;
        }else{
            lastX = X = xMiddle;
            lastY = (Math.pow(x1,2)+Math.pow(y1,2)-Math.pow(x3,2)-Math.pow(y3,2)+2*lastX*(x3-x1))/(2*(y1-y3));
        }
        return "("+lastX+","+lastY+")";
    }
}
