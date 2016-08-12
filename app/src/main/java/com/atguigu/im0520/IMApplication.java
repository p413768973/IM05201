package com.atguigu.im0520;

import android.app.Application;
import android.content.Context;

import com.atguigu.im0520.model.Modle;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

/**
 * Created by Administrator on 2016/8/2 0002.
 */
public class IMApplication extends Application {
    private static  Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化EaseUI
        initEaseUI();

        // 初始化模型层数据
        Modle.getInstance().init(this);

        mContext = this;
    }

    // 获取全局上下文
    public static Context getmContext(){
        return mContext;
    }

    private void initEaseUI() {
        EMOptions options = new EMOptions();

        // 不是总是接受邀请信息
        options.setAcceptInvitationAlways(false);
        // 不是自动接受群邀请信息
        options.setAutoAcceptGroupInvitation(false);

        EaseUI.getInstance().init(this,options);
    }
}
