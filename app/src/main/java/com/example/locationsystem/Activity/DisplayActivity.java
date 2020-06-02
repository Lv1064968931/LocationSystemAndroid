package com.example.locationsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationsystem.Adapter.LocationAdapter;
import com.example.locationsystem.Entity.Location;
import com.example.locationsystem.Global.TargetUrl;
import com.example.locationsystem.R;
import com.example.locationsystem.Utils.JSONTOOL;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.locationsystem.Global.DirectionInfo.displaylocation;
import static com.example.locationsystem.Global.DirectionInfo.locations;

public class DisplayActivity extends AppCompatActivity {
    private WindowManager manager;

    ListView displayListview;

    private LocationAdapter locationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.height = (int) (display.getHeight() * 0.6);
        layoutParams.width = (int) (display.getWidth() * 0.8);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        getWindow().setAttributes(layoutParams);

        String requestdeviceAddress = TargetUrl.targetUrl+"/api/select_all_device";
        requestDeviceData(requestdeviceAddress);

    }

    private void bindViews() {
        displayListview = (ListView) findViewById(R.id.display_listview);

        final String location[]= new String[]{"书店","酒店","便利店"};
        locationAdapter = new LocationAdapter(locations,DisplayActivity.this);
        displayListview.setAdapter(locationAdapter);
        displayListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(DisplayActivity.this,MainActivity.class);
                startActivity(intent);
                displaylocation = location[position];
                System.out.println("location111222222"+location[position]);
                locations.clear();
            }
        });
    }




    @Override
    public void onBackPressed() {
        locations.clear();
        finish();
        Log.i("xxxxxxxxxxxxxxxxx","销毁活动");
    }


    //请求设备数据
    private void  requestDeviceData(String address){
        OkHttpUtils.post()
                .url(address)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(DisplayActivity.this,"网络错误，请检查网络连接或关闭防火墙",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<HashMap<String,String>> mapList = JSONTOOL.analyze_some_json(response);
                        for(int i=0;i<mapList.size();i++){
                            Location location = new Location(mapList.get(i).get("beaconName"),mapList.get(i).get("location"),Double.parseDouble(mapList.get(i).get("x")),Double.parseDouble(mapList.get(i).get("y")));
                            locations.add(location);
                        }
                        bindViews();
                        System.out.println("maplist1111111111111"+locations);
                    }
                });
    }

}
