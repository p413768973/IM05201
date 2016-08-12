package com.atguigu.im0520.controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.atguigu.im0520.R;
import com.atguigu.im0520.controller.fragment.ChatFragment;
import com.atguigu.im0520.controller.fragment.ContactListFragment;
import com.atguigu.im0520.controller.fragment.SetttingFragment;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactListFragment contactListFragment;
    private SetttingFragment setttingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();

        initData();

        initListener();
    }

    private void initData() {
        chatFragment = new ChatFragment();

        contactListFragment = new ContactListFragment();

        setttingFragment = new SetttingFragment();
    }

    private void initListener() {
        // 监听RadioGroup的选择事件
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = null;

                switch (checkedId){
                    case R.id.rb_main_chat:// 会话
                        fragment = chatFragment;
                        break;

                    case R.id.rb_main_contact:// 联系人的
                        fragment = contactListFragment;
                        break;

                    case R.id.rb_main_setting:// 设置的
                        fragment = setttingFragment;
                        break;
                }

                switchFragment(fragment);
            }
        });

        // 默认选择会话页面
        rg_main.check(R.id.rb_main_chat);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fl_main,fragment).commit();

    }

    private void initView() {
        rg_main = (RadioGroup)findViewById(R.id.rg_main);
    }
}
