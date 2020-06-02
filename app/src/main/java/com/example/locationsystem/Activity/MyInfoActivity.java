package com.example.locationsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationsystem.R;
import com.example.locationsystem.Utils.JSONTOOL;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;

import static com.example.locationsystem.Global.DirectionInfo.globalPassword;
import static com.example.locationsystem.Global.DirectionInfo.globalUsername;

public class MyInfoActivity extends AppCompatActivity {

    private TextView myinfoUsernameTV,myinfoPasswordTV;
    private Button exitBtn;
    private Button displayBtn;
    Mylistener mylistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        bindviews();
    }

    //UI组件初始化与事件绑定
    private void bindviews(){
        myinfoUsernameTV = (TextView)findViewById(R.id.myinfo_usernameTV);
        myinfoPasswordTV = (TextView)findViewById(R.id.myinfo_passwordTV);
        exitBtn = (Button)findViewById(R.id.exit_btn);
        displayBtn = (Button)findViewById(R.id.display_btn);

        mylistener = new Mylistener();
        exitBtn.setOnClickListener(mylistener);
        displayBtn.setOnClickListener(mylistener);

        myinfoUsernameTV.setText(globalUsername);
        myinfoPasswordTV.setText(globalPassword);
    }

    //监听事件
    private class Mylistener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.exit_btn:
                    globalUsername="";
                    globalPassword="";
                    Intent intent = new Intent(MyInfoActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.display_btn:
                    Intent displayIntent = new Intent(MyInfoActivity.this,DisplayActivity.class);
                    startActivity(displayIntent);
                    break;
            }
        }
    }


}
