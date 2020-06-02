package com.example.locationsystem.Global;

import com.example.locationsystem.Entity.Location;

import java.util.ArrayList;

public class DirectionInfo {
    public static String globalPassword;
    public static  String globalUsername;
    public static String NORTH = "正北";
    public static String SOUTH = "正南";
    public static String WEST = "正西";
    public static String EAST = "正东";
    public static String NORTHWEST = "西北";
    public static String NORTHEAST = "东北";
    public static String SOUTHWEST = "西南";
    public static String SOUTHEAST = "东南";
    public static double sourceX;
    public static double sourceY;
    public static double targetX;
    public static double targetY;
    public static String globalPath;

    public static ArrayList<Location> locations= new ArrayList<Location>();

    public static String displaylocation;
}
