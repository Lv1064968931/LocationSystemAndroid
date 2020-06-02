package com.example.locationsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationsystem.R;
import com.example.locationsystem.Utils.ShortestDijkstra;

import static com.example.locationsystem.Global.DirectionInfo.globalPath;
import static com.example.locationsystem.Utils.BeaconUtils.navigationPath;
import static com.example.locationsystem.Utils.ShortestDijkstra.globalShortestPaths;
import static com.example.locationsystem.Utils.ShortestDijkstra.shortestPath;
import static com.example.locationsystem.Utils.ShortestDijkstra.shortestPathDes;
import static com.example.locationsystem.Utils.ShortestDijkstra.startPoint;

public class FloatActivity extends AppCompatActivity {

    private WindowManager manager;
    private TextView locateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float);

        manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.height = (int) (display.getHeight() * 0.4);
        layoutParams.width = (int) (display.getWidth() * 0.6);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        getWindow().setAttributes(layoutParams);

        bindviews();

    }
    //UI组件初始化与事件绑定
    private void bindviews(){
        locateTV = (TextView)findViewById(R.id.locate_TV);
        locateTV.setText(navigationPath(globalPath));
    }

    @Override
    public void onBackPressed() {
        finish();
        Log.i("xxxxxxxxxxxxxxxxx","销毁活动");
    }

}
