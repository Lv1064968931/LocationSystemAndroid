package com.example.locationsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    private EditText usernameET,passwordET;
    private ImageButton loginBtn;
    private TextView forgetPassTV,registerTV;
    Mylistener mylistener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindviews();
    }

    //UI组件初始化与事件绑定
    private void bindviews(){

        usernameET = (EditText)findViewById(R.id.username_ET);
        passwordET = (EditText)findViewById(R.id.password_ET);
        loginBtn = (ImageButton)findViewById(R.id.login_btn);
        forgetPassTV = (TextView)findViewById(R.id.forgetPass_TV);
        registerTV = (TextView)findViewById(R.id.register_TV);

        //输入内容后按钮变色
        CheckEditForButton checkEditForButton = new CheckEditForButton(loginBtn);
        checkEditForButton.addEditText(usernameET,passwordET);
        checkEditForButton.setListener(new EditTextChangeListener() {
           @Override
           public void allHasContent(boolean isHasContent) {
               if(isHasContent){
                   loginBtn.setSelected(true);
               }else{
                   loginBtn.setSelected(false);
               }
           }
       });

        mylistener = new Mylistener();
        loginBtn.setOnClickListener(mylistener);
        forgetPassTV.setOnClickListener(mylistener);
        registerTV.setOnClickListener(mylistener);

    }

    private class Mylistener implements View.OnClickListener{

        Intent intent;

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.login_btn:
                    String loginAddress = TargetUrl.targetUrl+"/api/client_login";
                    login(loginAddress,usernameET.getText().toString(),passwordET.getText().toString());
                    break;
                case R.id.forgetPass_TV:
                    break;
                case R.id.register_TV:
                    intent = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    }

    //登录方法
    public void login(String address, final String username, String password){

        OkHttpUtils.post()
                .url(address)
                .addParams("username",username)
                .addParams("password",password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(LoginActivity.this,"网络错误，请检查网络连接或关闭防火墙",Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        final HashMap<String,String> map = JSONTOOL.analyze_once_json(response);
                        final HashMap<String,String> usermap = JSONTOOL.analyze_once_json(map.get("data"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(map.get("code").equals("200")){
                                    Toast.makeText(LoginActivity.this,map.get("message"),Toast.LENGTH_LONG).show();
                                    DirectionInfo.globalUsername = usermap.get("username");
                                    DirectionInfo.globalPassword = usermap.get("password");
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(LoginActivity.this,map.get("message"),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
    }

}
