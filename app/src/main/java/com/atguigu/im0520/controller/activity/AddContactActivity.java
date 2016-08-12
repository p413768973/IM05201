package com.atguigu.im0520.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.im0520.R;
import com.atguigu.im0520.model.Modle;
import com.atguigu.im0520.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

// 添加联系人页面
public class AddContactActivity extends Activity {
    private TextView tv_add_contact_find;
    private EditText et_add_contact;
    private LinearLayout ll_add_contact;
    private TextView tv_add_contact_name;
    private Button bt_add_contact;
    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initView();

        initListener();
    }

    private void initListener() {
        // 查找按钮的点击事件
        tv_add_contact_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFind();
            }
        });

        // 添加按钮的点击事件
        bt_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAdd();
            }
        });
    }

    private void buttonAdd() {
        // 联网
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(mUserInfo.getHxid(),"添加好友");

                    // 提示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "发送添加好友邀请成功", Toast.LENGTH_SHORT).show();
                            ll_add_contact.setVisibility(View.GONE);
                            et_add_contact.setText("");
                        }
                    });


                } catch (HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "发送添加好友邀请失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void buttonFind() {
        // 获取输入的名称
        String name = et_add_contact.getText().toString();
        // 判断是否为空
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(AddContactActivity.this, "输入的名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 判断服务器有没有这个人
        mUserInfo = new UserInfo(name);

        // 显示查找到的信息
        ll_add_contact.setVisibility(View.VISIBLE);

        // 显示查找到的人的名称
        tv_add_contact_name.setText(mUserInfo.getName());

    }

    private void initView() {
        tv_add_contact_find = (TextView)findViewById(R.id.tv_add_contact_find);
        et_add_contact = (EditText)findViewById(R.id.et_add_contact);
        ll_add_contact = (LinearLayout)findViewById(R.id.ll_add_contact);
        tv_add_contact_name = (TextView)findViewById(R.id.tv_add_contact_name);
        bt_add_contact = (Button)findViewById(R.id.bt_add_contact);

        ll_add_contact.setVisibility(View.GONE);
    }
}
