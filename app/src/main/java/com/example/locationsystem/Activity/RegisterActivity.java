package com.example.locationsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.locationsystem.Global.TargetUrl;
import com.example.locationsystem.Global.DirectionInfo;
import com.example.locationsystem.R;
import com.example.locationsystem.Utils.CheckEditForButton;
import com.example.locationsystem.Utils.EditTextChangeListener;
import com.example.locationsystem.Utils.JSONTOOL;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsernameET,registerPasswordET;
    private ImageButton registerBtn;
    Mylistener mylistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindviews();
    }

    //UI组件初始化与事件绑定
    private void bindviews(){
        registerUsernameET = (EditText)findViewById(R.id.register_username_ET);
        registerPasswordET = (EditText)findViewById(R.id.register_password_ET);
        registerBtn = (ImageButton) findViewById(R.id.register_btn);

        //输入内容后按钮变色
        CheckEditForButton checkEditForButton = new CheckEditForButton(registerBtn);
        checkEditForButton.addEditText(registerUsernameET,registerPasswordET);
        checkEditForButton.setListener(new EditTextChangeListener() {
            @Override
            public void allHasContent(boolean isHasContent) {
                if(isHasContent){
                    registerBtn.setSelected(true);
                }else{
                    registerBtn.setSelected(false);
                }
            }
        });
        mylistener = new Mylistener();
        registerBtn.setOnClickListener(mylistener);
    }

    //监听器类
    private class Mylistener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.register_btn:
                    String registerAddress = TargetUrl.targetUrl+"/api/client_register";
                    register(registerAddress,registerUsernameET.getText().toString().trim(),registerPasswordET.getText().toString());
                    break;
            }
        }
    }

    //注册方法
    private void register(String address, String username,String password){
        OkHttpUtils.post()
                .url(address)
                .addParams("username",username)
                .addParams("password",password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(RegisterActivity.this,"网络错误，请检查网络连接或关闭防火墙",Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        final HashMap<String,String> map = JSONTOOL.analyze_once_json(response);
                        final HashMap<String,String> usermap = JSONTOOL.analyze_once_json(map.get("data"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(map.get("code").equals("200")){
                                    Toast.makeText(RegisterActivity.this,map.get("message"),Toast.LENGTH_LONG).show();
                                    DirectionInfo.globalUsername = usermap.get("username");
                                    DirectionInfo.globalPassword = usermap.get("password");
                                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(RegisterActivity.this,map.get("message"),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
    }
}
