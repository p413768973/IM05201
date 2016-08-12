package com.atguigu.im0520.controller.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atguigu.im0520.R;
import com.atguigu.im0520.model.Modle;
import com.atguigu.im0520.model.bean.UserInfo;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends Activity {
    private EditText et_login_name;
    private EditText et_login_pwd;
    private Button bt_login_regist;
    private Button bt_login_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initView();


        initListener();
    }

    private void initListener() {
        // 注册按钮的点击事件
        bt_login_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist();
            }
        });

        // 登录按钮的点击事件
        bt_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }


    // 登录业务逻辑处理
    private void login() {
        // 获取输入的用户名和密码
        final String loginName = et_login_name.getText().toString();
        final String loginPwd = et_login_pwd.getText().toString();

        //校验
        if(TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            Toast.makeText(LoginActivity.this, "登录的用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 去环信服务器注册
        // 显示加载进度条
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("正在登录中");
        pd.show();

        // 访问网络
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去环信服务器登录
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        // 关闭进度条
                        pd.cancel();

                        // 保存用户名到本地数据库
                        Modle.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));

                        // 登录成功后的初始化处理
                        Modle.getInstance().loginSuccess(new UserInfo(loginName));

                        // 跳转到主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        // 结束当前页面
                        finish();
                    }

                    @Override
                    public void onError(int i, final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 提示
                                Toast.makeText(LoginActivity.this, "登录失败" + s, Toast.LENGTH_SHORT).show();
                                // 隐藏进度条
                                pd.cancel();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    // 注册按钮的事件处理
    private void regist() {
        // 获取输入的用户名和密码
        final String registName = et_login_name.getText().toString();
        final String registPwd = et_login_pwd.getText().toString();

        //校验
        if(TextUtils.isEmpty(registName) || TextUtils.isEmpty(registPwd)) {
            Toast.makeText(LoginActivity.this, "注册的用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 去环信服务器注册
        // 显示加载进度条
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("正在注册中");
        pd.show();

        // 访问网络
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去环信服务器注册
                try {
                    EMClient.getInstance().createAccount(registName, registPwd);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                            // 关闭进度条
                            pd.cancel();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败"+e.toString(), Toast.LENGTH_SHORT).show();

                            // 关闭进度条
                            pd.cancel();
                        }
                    });
                }
            }
        });



    }

    private void initView() {
        et_login_name = (EditText)findViewById(R.id.et_login_name);
        et_login_pwd = (EditText)findViewById(R.id.et_login_pwd);
        bt_login_regist = (Button)findViewById(R.id.bt_login_regist);
        bt_login_login = (Button)findViewById(R.id.bt_login_login);
    }
}
