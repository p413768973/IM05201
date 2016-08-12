package com.atguigu.im0520.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.atguigu.im0520.IMApplication;
import com.atguigu.im0520.R;
import com.atguigu.im0520.controller.activity.AddContactActivity;
import com.atguigu.im0520.controller.activity.ChatActivity;
import com.atguigu.im0520.controller.activity.GroupListActivity;
import com.atguigu.im0520.controller.activity.InviteActivity;
import com.atguigu.im0520.model.Modle;
import com.atguigu.im0520.model.bean.UserInfo;
import com.atguigu.im0520.utils.Constant;
import com.atguigu.im0520.utils.SpUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
public class ContactListFragment extends EaseContactListFragment {

    private LocalBroadcastManager mLBM;
    // 邀请信息变化的广播
    private BroadcastReceiver InviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 联系人邀请信息的变化  || 群邀请信息的变化
            if (intent.getAction() == Constant.CONTACT_INVITE_CHANGED || intent.getAction() == Constant.GROUP_INVITE_CHANGED) {
                // 显示红点
                iv_head_contact_invite.setVisibility(View.VISIBLE);
            }
        }
    };
    private ImageView iv_head_contact_invite;
    private BroadcastReceiver ContactChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == Constant.CONTACT_CHANGED) {
                // 刷新联系人页面
                refrshContact();
            }
        }
    };
    private String mHxid;

    @Override
    protected void initView() {
        super.initView();

        // 添加头布局
        View headerView = View.inflate(getActivity(), R.layout.fragment_head_contact, null);

        listView.addHeaderView(headerView);

        // 添加加号
        titleBar.setRightImageResource(R.drawable.em_add);

        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到添加联系人页面
                Intent intent = new Intent(getActivity(), AddContactActivity.class);

                startActivity(intent);
            }
        });

        // 红点处理
        iv_head_contact_invite = (ImageView) headerView.findViewById(R.id.iv_head_contact_invite);

        boolean isNewInvite = SpUtil.getInstance(IMApplication.getmContext()).getBoolean(SpUtil.IS_NEW_INVITE, false);
        // 设置红点是否显示
        iv_head_contact_invite.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        // 邀请信息条目的点击事件
        LinearLayout ll_header_contact_invite = (LinearLayout) headerView.findViewById(R.id.ll_header_contact_invite);
        ll_header_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏红点
                iv_head_contact_invite.setVisibility(View.GONE);

                SpUtil.getInstance(IMApplication.getmContext()).save(SpUtil.IS_NEW_INVITE, false);

                // 跳转到邀请信息列表页面
                Intent intent = new Intent(getActivity(), InviteActivity.class);

                startActivity(intent);
            }
        });

        // listview条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {

                if(user == null) {
                    return;
                }
                // 跳转到会话页面
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                // 传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());

                startActivity(intent);
            }
        });

        // 群组条目的点击事件
        LinearLayout ll_header_contact_group = (LinearLayout) headerView.findViewById(R.id.ll_header_contact_group);

        ll_header_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到群组列表页面
                Intent intent = new Intent(getActivity(), GroupListActivity.class);

                startActivity(intent);
            }
        });

    }

    @Override
    protected void setUpView() {
        super.setUpView();

        // 注册绑定listview
        registerForContextMenu(listView);

        // 从环信服务器获取联系人信息
        getContactsFromHxServer();

        mLBM = LocalBroadcastManager.getInstance(getActivity());
        // 联系人邀请信息变化
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        // 联系变化
        mLBM.registerReceiver(ContactChangedReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        // 群邀请信息的变化
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {// f4:显示这个接口的实现类
        super.onCreateContextMenu(menu, v, menuInfo);

        int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        EaseUser user = (EaseUser) listView.getItemAtPosition(position);
        mHxid = user.getUsername();

        // 获取布局
        getActivity().getMenuInflater().inflate(R.menu.delete_contact, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 当前选择的是删除这个条目
        if(item.getItemId() == R.id.delete_contact) {
            deleteContact();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    // 删除联系人
    private void deleteContact() {
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 服务器删除
                    EMClient.getInstance().contactManager().deleteContact(mHxid);
                    // 本地数据库
                    Modle.getInstance().getmDbManager().getContactDao().deleteContactByHxId(mHxid);
                    Modle.getInstance().getmDbManager().getInvitationDao().removeInvitation(mHxid);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 刷新列表
                            refrshContact();
                            // 提示
                            Toast.makeText(getActivity(), "删除"+mHxid, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getContactsFromHxServer() {
        // 联网
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // 从环信服务器获取联系人数据
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    if (hxids != null && hxids.size() >= 0) {

                        // 联系人和环信id的转换
                        List<UserInfo> contacts = new ArrayList<UserInfo>();

                        for (String hxid : hxids) {
                            UserInfo userInfo = new UserInfo(hxid);

                            contacts.add(userInfo);
                        }

                        // 保存联系人到本地数据库
                        Modle.getInstance().getmDbManager().getContactDao().saveContacts(contacts, true);

                        if(getActivity() == null) {
                            return;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 页面刷新
                                refrshContact();
                                // 提示
                                Toast.makeText(getActivity(), "获取联系人成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 提示
                            Toast.makeText(getActivity(), "获取联系人失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void refrshContact() {

        // 从本地数据库中获取数据
        List<UserInfo> contacts = Modle.getInstance().getmDbManager().getContactDao().getContacts();

        if (contacts != null && contacts.size() >= 0) {
            // 设置数据
            Map<String, EaseUser> contactMap = new HashMap<>();

            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxid());

                contactMap.put(contact.getHxid(), easeUser);
            }

            setContactsMap(contactMap);
            // 通知刷新
            refresh();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 解注册广播
        mLBM.unregisterReceiver(ContactChangedReceiver);
        mLBM.unregisterReceiver(InviteChangedReceiver);
    }
}
