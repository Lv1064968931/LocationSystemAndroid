package com.example.locationsystem.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationsystem.Entity.BeaconClass;
import com.example.locationsystem.Global.TargetUrl;
import com.example.locationsystem.R;
import com.example.locationsystem.SelfDesignComponent.DragFloatActionButton;
import com.example.locationsystem.Utils.BeaconManager;
import com.example.locationsystem.Utils.JSONTOOL;
import com.example.locationsystem.Utils.ShortestDijkstra;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.locationsystem.Global.DirectionInfo.displaylocation;
import static com.example.locationsystem.Global.DirectionInfo.globalPath;
import static com.example.locationsystem.Global.DirectionInfo.sourceX;
import static com.example.locationsystem.Global.DirectionInfo.sourceY;
import static com.example.locationsystem.Global.DirectionInfo.targetX;
import static com.example.locationsystem.Global.DirectionInfo.targetY;
import static com.example.locationsystem.Utils.BeaconUtils.confirmDirection;
import static com.example.locationsystem.Utils.BeaconUtils.confirmXY;
import static com.example.locationsystem.Utils.ShortestDijkstra.globalShortestPaths;
import static com.example.locationsystem.Utils.ShortestDijkstra.shortestPathDes;
import static com.example.locationsystem.Utils.ShortestDijkstra.startPoint;

public class MainActivity extends AppCompatActivity implements BeaconManager.BeaconListener {

    BeaconManager beaconManager;
    ShortestDijkstra shortestDijkstra;
    Mylistener mylistener;

    private ImageButton deviceBtn1,deviceBtn2,deviceBtn3,locateBtn;
    DragFloatActionButton mBtn;
    ListView listView;
    TextView mytv_xy,navigationTV;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    Intent intent;

    ArrayList<BeaconClass.Beacon> beacons;
    Map<String,BeaconClass.Beacon> scanedBeacons;
    ArrayList<Double>distances;
    List<HashMap<String,String>> mapList;
    String path1,path2,path3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        bindviews();



    }

    //UI组件初始化与事件绑定
    private void bindviews(){
        deviceBtn1 = (ImageButton)findViewById(R.id.device_btn1);
        deviceBtn2 = (ImageButton)findViewById(R.id.device_btn2);
        deviceBtn3 = (ImageButton)findViewById(R.id.device_btn3);
        locateBtn = (ImageButton)findViewById(R.id.locate_btn);
        listView = (ListView)findViewById(R.id.listview);
        mytv_xy = (TextView)findViewById(R.id.mytv_xy);
        navigationTV = (TextView) findViewById(R.id.navigation_tv);

        mBtn = findViewById(R.id.img_btn);

        beacons=new ArrayList<BeaconClass.Beacon>();
        scanedBeacons=new HashMap<String, BeaconClass.Beacon>();
        distances=new ArrayList<Double>();

        shortestDijkstra = new ShortestDijkstra();

        beaconManager=new BeaconManager(this);
        beaconManager.setBeaconListener(this);


        String requestdeviceAddress = TargetUrl.targetUrl+"/api/select_all_device";
        requestDeviceData(requestdeviceAddress);
        Toast.makeText(MainActivity.this,"开始定位...",Toast.LENGTH_LONG).show();

        mylistener = new Mylistener();

        mBtn.setOnClickListener(mylistener);
        deviceBtn1.setOnClickListener(mylistener);
        deviceBtn2.setOnClickListener(mylistener);
        deviceBtn3.setOnClickListener(mylistener);
        locateBtn.setOnClickListener(mylistener);
        listView.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("location111"+displaylocation);
        if(displaylocation!=null) {
            switch (displaylocation) {
                case "书店":
                    setSimulateClick(deviceBtn2,deviceBtn2.getWidth()/2-deviceBtn2.getX(),deviceBtn2.getHeight()/2+deviceBtn2.getY());
                    displaylocation=null;
                    break;
                case "便利店":
                    setSimulateClick(deviceBtn1,deviceBtn1.getWidth()/2-deviceBtn1.getX(),deviceBtn1.getHeight()/2+deviceBtn1.getY());
                    displaylocation=null;
                    break;
                case "酒店":
                    setSimulateClick(deviceBtn3,deviceBtn3.getWidth()/2-deviceBtn3.getX(),deviceBtn3.getHeight()/2+deviceBtn3.getY());
                    displaylocation=null;
                    break;
            }
        }
    }

    //MotionEvent模拟点击
    private void setSimulateClick(View view, float x, float y) {

        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.stopScan();
    }

    @Override
    public void onScanBeacon(BeaconClass.Beacon beacon) {
        String key=beacon.uuid+beacon.major+"-"+beacon.minor;
        if(scanedBeacons.containsKey(key)){
            BeaconClass.Beacon mbeacon=scanedBeacons.get(key);
            mbeacon.rssi=beacon.rssi;
            mbeacon.distance=beacon.distance;
            distances.clear();
        }else{
            scanedBeacons.put(key, beacon);
            beacons.add(beacon);
            for(int i=0;i<mapList.size();i++){

                for(BeaconClass.Beacon beacon1:beacons) {

                    if (beacon1.beaconName != null) {
                        if (beacon1.beaconName.equals(mapList.get(i).get("beaconName"))) {
                            beacon1.x = Double.parseDouble(mapList.get(i).get("x"));
                            beacon1.y = Double.parseDouble(mapList.get(i).get("y"));
                            beacon1.location = mapList.get(i).get("location");

                        }
                    }

                }
            }
        }
      adapter.notifyDataSetChanged();
    }

    //请求设备数据
    private void  requestDeviceData(String address){
        OkHttpUtils.post()
                .url(address)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MainActivity.this,"网络错误，请检查网络连接或关闭防火墙",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mapList = JSONTOOL.analyze_some_json(response);
                    }
                });
    }

    //弹出菜单的dialog自定义
    private void dialogselfDesign(String distance_message){

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this,R.style.DialogStyle)
                .setTitle("信息")
                .setMessage(distance_message)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent(MainActivity.this,FloatActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
        //修改“确认”、“取消”按钮的字体大小
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    private BaseAdapter adapter=new BaseAdapter() {
        @Override
        public int getCount() {
            return beacons.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.list_item_beacon, null);
                // 得到控件对象
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                holder.tv_uuid = (TextView) convertView
                        .findViewById(R.id.tv_uuid);
                holder.tv_major = (TextView) convertView
                        .findViewById(R.id.tv_major);
                holder.tv_minor = (TextView) convertView
                        .findViewById(R.id.tv_minor);

                holder.tv_rssi = (TextView) convertView
                        .findViewById(R.id.tv_rssi);
                holder.tv_distance = (TextView) convertView
                        .findViewById(R.id.tv_distance);
                convertView.setTag(holder);
                holder.tv_xy = (TextView) convertView
                        .findViewById(R.id.tv_xy);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            BeaconClass.Beacon beacon=beacons.get(position);
            if(beacon.major!=1){
                beacons.remove(position);
            }else {
                holder.tv_name.setText(beacon.beaconName);
                holder.tv_major.setText(String.valueOf(beacon.major));
                holder.tv_minor.setText(String.valueOf(beacon.minor));
                holder.tv_rssi.setText(String.valueOf(beacon.rssi));
                holder.tv_uuid.setText(beacon.uuid);
                holder.tv_distance.setText(String.valueOf(beacon.distance));
                holder.tv_xy.setText(String.valueOf("(" + beacon.x + "," + beacon.y + ")"));
                distances.add(beacon.distance);

                if (distances.size() == 3) {
                    mytv_xy.setText(confirmXY(distances));
                    shortestDijkstra.createGraph1(4, distances);
                    shortestDijkstra.dijkstra(0);
                    navigationTV.setText(shortestDijkstra.startPoint + "\n" + shortestPathDes);
                    shortestPathDes = "";
                    path1 = globalShortestPaths.get(1);
                    path2 = globalShortestPaths.get(2);
                    path3 = globalShortestPaths.get(3);
                    globalShortestPaths.clear();
                }
            }
            return convertView;
        }

    };
    private class ViewHolder {
        TextView tv_name;
        TextView tv_uuid;
        TextView tv_major;
        TextView tv_minor;
        TextView tv_rssi;
        TextView tv_distance;
        TextView tv_xy;
    }



    private class Mylistener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.device_btn1:
                    for(BeaconClass.Beacon beacon1:beacons){
                        if (beacon1.beaconName.equals("abeacon_F627")) {
                            if (beacon1.distance < 1) {
                                    Toast.makeText(MainActivity.this,"导航已结束",Toast.LENGTH_LONG).show();
                            } else {
                                targetX = beacon1.x;
                                targetY = beacon1.y;
                                String distanceMessage = "您选择的目的地为：" + beacon1.location + ",您选择的目的地距离您" + beacon1.distance + "米。" + "位于您的"
                                        + confirmDirection(sourceX, sourceY, targetX, targetY) + "方向";
                                globalPath = path2;
                                dialogselfDesign(distanceMessage);
                            }
                        }
                    }
                    break;
                case R.id.device_btn2:
                    for(BeaconClass.Beacon beacon1:beacons){
                        if (beacon1.beaconName.equals("abeacon_0710")){
                            if (beacon1.distance < 1) {
                                Toast.makeText(MainActivity.this,"导航已结束",Toast.LENGTH_LONG).show();
                            } else {
                                targetX = beacon1.x;
                                targetY = beacon1.y;
                                String distanceMessage = "您选择的目的地为：" + beacon1.location + ",您选择的目的地距离您" + beacon1.distance + "米。" + "位于您的"
                                        + confirmDirection(sourceX, sourceY, targetX, targetY) + "方向";
                                globalPath = path1;
                                dialogselfDesign(distanceMessage);
                            }
                        }
                    }
                    break;
                case R.id.device_btn3:
                    for(BeaconClass.Beacon beacon1:beacons){
                        if (beacon1.beaconName.equals("abeacon_D8BC")){
                            if (beacon1.distance < 1) {
                                Toast.makeText(MainActivity.this,"导航已结束",Toast.LENGTH_LONG).show();
                            } else {
                                targetX = beacon1.x;
                                targetY = beacon1.y;
                                String distanceMessage = "您选择的目的地为：" + beacon1.location + ",您选择的目的地距离您" + beacon1.distance + "米。" + "位于您的"
                                        + confirmDirection(sourceX, sourceY, targetX, targetY) + "方向";
                                globalPath = path3;
                                dialogselfDesign(distanceMessage);
                            }
                        }
                    }
                    break;
                case R.id.locate_btn:
                    beacons.clear();
                    scanedBeacons.clear();

                    adapter.notifyDataSetChanged();
                    beaconManager.startScanBeacon();

                    break;
                case R.id.img_btn:
                    intent = new Intent(MainActivity.this,MyInfoActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
