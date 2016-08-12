package com.atguigu.im0520.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.atguigu.im0520.R;
import com.atguigu.im0520.controller.activity.LoginActivity;
import com.atguigu.im0520.model.Modle;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
public class SetttingFragment extends Fragment {
    private Button bt_setting_logout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        bt_setting_logout = (Button) view.findViewById(R.id.bt_setting_logout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    private void initData() {
        // 显示当前用户
        // 获取当前用户名
        String currentUser = EMClient.getInstance().getCurrentUser();

        bt_setting_logout.setText("退出登录（" + currentUser + ")");

        // 点击事件处理
        bt_setting_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {

        // 联网
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去环信服务器退出登录
                EMClient.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {

                        // 关闭联系人和邀请信息的数据库
                        Modle.getInstance().getmDbManager().close();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 提示
                                Toast.makeText(getActivity(), "退出登录成功", Toast.LENGTH_SHORT).show();
                                // 跳转到登录页面
                                Intent intent = new Intent(getActivity(), LoginActivity.class);

                                startActivity(intent);

                                // 关闭当前页面
                                getActivity().finish();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, final String s) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "退出登录失败" + s, Toast.LENGTH_SHORT).show();
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
}
