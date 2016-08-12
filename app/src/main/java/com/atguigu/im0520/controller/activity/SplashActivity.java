package com.atguigu.im0520.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.atguigu.im0520.R;
import com.atguigu.im0520.model.Modle;
import com.atguigu.im0520.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

// 欢迎页面
public class SplashActivity extends Activity {

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            toMainOrLogin();
        }
    };


    private void toMainOrLogin() {

        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 判断是否登录
                if(EMClient.getInstance().isLoggedInBefore()){// 登录过

                    // 获取当前用户数据
                    UserInfo account = Modle.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());
                    // 登录成功后的初始化处理

                    if(account == null) {
                        // 跳转到登录页面
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

                        startActivity(intent);
                    }else {
                        Modle.getInstance().loginSuccess(account);

                        // 跳转到主页面
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }else {
                    // 跳转到登录页面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

                    startActivity(intent);
                }

                // 结束当前页面
                finish();

            }
        });
//        new Thread(){
//            public void run(){
//
//            }
//        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // 延时二秒跳转到主页面或登录页面
        handler.sendMessageDelayed(Message.obtain(), 2000);
    }

}
