package com.atguigu.im0520.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.Toast;

import com.atguigu.im0520.R;
import com.atguigu.im0520.controller.adapter.InviteAdapter;
import com.atguigu.im0520.model.Modle;
import com.atguigu.im0520.model.bean.InvitationInfo;
import com.atguigu.im0520.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

// 邀请信息列表页面
public class InviteActivity extends Activity {
    private ListView lv_invite;

    private InviteAdapter.OnInviteListener mOnInviteListener = new InviteAdapter.OnInviteListener() {
        // 点击接受按钮的操作
        @Override
        public void onAccept(final InvitationInfo invitationInfo) {
            // 联网
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invitationInfo.getUser().getHxid());

                        // 更新数据库
                        Modle.getInstance().getmDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT, invitationInfo.getUser().getHxid());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 提示
                                Toast.makeText(InviteActivity.this, "您接受了" + invitationInfo.getUser().getName() + "的邀请", Toast.LENGTH_SHORT).show();

                                // 刷新页面
                                inviteRefresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 提示
                                Toast.makeText(InviteActivity.this, "接受失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 点击拒绝按钮的操作
        @Override
        public void onReject(final InvitationInfo invitationInfo) {
            // 联网
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invitationInfo.getUser().getHxid());

                        // 更新数据库
                        Modle.getInstance().getmDbManager().getInvitationDao().removeInvitation(invitationInfo.getUser().getHxid());


                        // 刷新+提示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新失败
                                inviteRefresh();

                                Toast.makeText(InviteActivity.this, "拒绝成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(InviteActivity.this, "拒绝失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 接受邀请
        @Override
        public void onInviteAccept(final InvitationInfo invitationInfo) {

            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 网络中修改
                        EMClient.getInstance().groupManager().acceptInvitation(invitationInfo.getGroup().getGroupId(),
                                invitationInfo.getGroup().getInvitePerson());

                        // 本地数据库要修改
                        Modle.getInstance().getmDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE,
                                invitationInfo.getGroup().getInvitePerson());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 页面刷新
                                inviteRefresh();

                                // 提示
                                Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 拒绝邀请
        @Override
        public void onInviteReject(final InvitationInfo invitationInfo) {
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().declineInvitation(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvitePerson(), "拒绝你");

                        // 本地数据库更新
                        Modle.getInstance().getmDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE, invitationInfo.getGroup().getInvitePerson());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 页面刷新
                                inviteRefresh();

                                // 提示
                                Toast.makeText(InviteActivity.this, "拒绝邀请", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 接受申请
        @Override
        public void onApplicationAccept(final InvitationInfo invitationInfo) {
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().acceptApplication(invitationInfo.getGroup().getGroupId(),
                                invitationInfo.getGroup().getInvitePerson());

                        // 本地数据库处理
                        Modle.getInstance().getmDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.GROUPO_ACCEPT_APPLICATION,
                                invitationInfo.getGroup().getInvitePerson());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 页面刷新
                                inviteRefresh();
                                
                                // 提示
                                Toast.makeText(InviteActivity.this, "接受申请成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受申请失败"+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 拒绝申请
        @Override
        public void onApplicationReject(final InvitationInfo invitationInfo) {

            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().declineApplication(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvitePerson(), "拒绝你的申请");

                        // 本地数据库处理
                        Modle.getInstance().getmDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION,
                                invitationInfo.getGroup().getInvitePerson());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 页面刷新
                                inviteRefresh();

                                // 提示
                                Toast.makeText(InviteActivity.this, "拒绝申请", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "失败"+ e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };
    private InviteAdapter inviteAdapter;
    private BroadcastReceiver InviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收到邀请信息变化的广播之后，刷新页面
            if (intent.getAction() == Constant.CONTACT_INVITE_CHANGED || intent.getAction() == Constant.GROUP_INVITE_CHANGED) {
                // 刷新
                inviteRefresh();
            }
        }
    };
    private LocalBroadcastManager mLBM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite);

        initView();

        initData();
    }

    private void initData() {
        // 创建适配器
        inviteAdapter = new InviteAdapter(this, mOnInviteListener);

        // 添加适配器到listview
        lv_invite.setAdapter(inviteAdapter);

        // 刷新数据
        inviteRefresh();

        // 监听邀请信息变化的广播
        mLBM = LocalBroadcastManager.getInstance(this);

        // 联系人邀请信息变化
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        // 群邀请信息变化
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    // 刷新方法
    private void inviteRefresh() {
        List<InvitationInfo> invitations = Modle.getInstance().getmDbManager().getInvitationDao().getInvitations();
        inviteAdapter.refresh(invitations);
    }

    private void initView() {
        lv_invite = (ListView) findViewById(R.id.lv_invite);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 解注册广播
        mLBM.unregisterReceiver(InviteChangedReceiver);
    }
}
