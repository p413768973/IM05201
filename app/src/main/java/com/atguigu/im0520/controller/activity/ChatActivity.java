package com.atguigu.im0520.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.atguigu.im0520.R;
import com.atguigu.im0520.utils.Constant;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

// 会话页面
public class ChatActivity extends FragmentActivity {

    private EaseChatFragment easeChatFragment;
    private String mHxid;
    private int mChatType;
    private LocalBroadcastManager mLBM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initData();

        initListener();
    }

    private void initListener() {
        // 会话页面的监听器
        easeChatFragment.setChatFragmentListener(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {

            }

            // 进入到会话详情（群详情）
            @Override
            public void onEnterToChatDetails() {
                // 跳转到群详情页面
                Intent intent = new Intent(ChatActivity.this, GroupDetailAcitivity.class);

                // 传递参数
                intent.putExtra(Constant.GROUP_ID, mHxid);

                startActivity(intent);
            }

            @Override
            public void onAvatarClick(String username) {

            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });

        // 退群广播处理
        if(mChatType == EaseConstant.CHATTYPE_GROUP) {
            mLBM = LocalBroadcastManager.getInstance(ChatActivity.this);

            BroadcastReceiver ExitGroupReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // 校验当前的环信id是否和传递过来的群Id一致
                    if(mHxid.equals(intent.getExtras().getString(Constant.GROUP_ID))) {
                        // 结束当前页面
                        finish();
                    }
                }
            };

            // 注册退群广播
            mLBM.registerReceiver(ExitGroupReceiver, new IntentFilter(Constant.EXIT_GROUP));
        }
    }

    private void initData() {
        // 创建会话的fragment
        easeChatFragment = new EaseChatFragment();

        // 获取环信id
        mHxid = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);

        // 获取会话类型
        mChatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE);

        // 设置参数
        easeChatFragment.setArguments(getIntent().getExtras());
        // 替换fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fl_chat, easeChatFragment).commit();
    }
}
