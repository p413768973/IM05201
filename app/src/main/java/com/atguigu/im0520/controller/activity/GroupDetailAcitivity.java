package com.atguigu.im0520.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.atguigu.im0520.R;
import com.atguigu.im0520.controller.adapter.GroupDetailAdapter;
import com.atguigu.im0520.model.Modle;
import com.atguigu.im0520.model.bean.UserInfo;
import com.atguigu.im0520.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

// 群详情页面
public class GroupDetailAcitivity extends Activity {
    private GridView gv_group_detail;
    private Button bt_group_detail;
    private EMGroup mGroup;

    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        //添加群成员的方法
        @Override
        public void onAddMembers() {
            // 跳转到选择联系人页面
            Intent intent = new Intent(GroupDetailAcitivity.this, PickContactAcitivity.class);

            // 传递群id
            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

            startActivityForResult(intent, 2);
        }

        @Override
        public void onDeleteMember(final UserInfo user) {

            // 联网
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        // 去环信服务器删除
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), user.getHxid());

                        // 刷新
                        getMembersFromHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailAcitivity.this, "删除"+user.getHxid()+"成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailAcitivity.this, "删除" + user.getHxid() + "失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(resultCode == RESULT_OK) {
            // 获取返回的好友
            final String[] memberses = data.getExtras().getStringArray("members");

            // 联网
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 去环信服务器添加好友邀请
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), memberses);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailAcitivity.this, "发送好友邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailAcitivity.this, "发送好友邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private GroupDetailAdapter mGroupDetailAdapter;
    private List<UserInfo> mUsers;
    private List<UserInfo> mUserss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_detail_acitivity);

        initView();

        initData();

        initListener();
    }

    private void initListener() {
        // gridview的触摸事件
        gv_group_detail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    // 按下
                    case MotionEvent.ACTION_DOWN:
                        // 如果当前是删除模式
                        if(mGroupDetailAdapter.ismIsDeleteModel()) {
                            // 切换回正常模式
                            mGroupDetailAdapter.setmIsDeleteModel(false);

                            // 通知刷新
                            mGroupDetailAdapter.notifyDataSetChanged();

                            return true;
                        }
                        break;


                }
                return false;
            }
        });
    }

    private void initData() {
        // 获取传递过来的数据
        String groupId = getIntent().getExtras().getString(Constant.GROUP_ID);

        if (groupId == null) {
            // 结束当前页面并返回
            finish();
            return;
        } else {
            // 获取到该群的所有信息
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }

        // button中群成员和群主不同显示
        initButtonDisplay();

        // 初始化gridview
        initGridview();
    }

    private void initGridview() {
        // 创建适配器
        // 如果当前用户是群主 或者是公开的  就可以添加和删除群成员
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();
        mGroupDetailAdapter = new GroupDetailAdapter(this, isCanModify, mOnGroupDetailListener);

        // 将适配器添加到gridview中
        gv_group_detail.setAdapter(mGroupDetailAdapter);

        // 刷新页面
        getMembersFromHxServer();
    }

    private void getMembersFromHxServer() {
       Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
           @Override
           public void run() {
               try {
                   EMGroup groupFromServer = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());

                   List<String> members = groupFromServer.getMembers();
                   
                   if(members != null && members.size() >=0) {

                       mUserss = new ArrayList<UserInfo>();

                       for (String member:members){
                           UserInfo userInfo = new UserInfo(member);
                           mUserss.add(userInfo);
                       }
                   }

                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           mGroupDetailAdapter.refresh(mUserss);

//                           Toast.makeText(GroupDetailAcitivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                       }
                   });

               } catch (HyphenateException e) {
                   e.printStackTrace();
               }
           }
       });
    }

    private void initButtonDisplay() {

        // 根据当前用户是否是群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {
            bt_group_detail.setText("解散群");

            // 解散群按钮的点击事件
            bt_group_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 去服务器解散
                    Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 从环信服务器中解散该群
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                // 发送解散群广播
                                sendExitGroupBroadCastReceiver();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 结束当前页面
                                        finish();

                                        // 提示
                                        Toast.makeText(GroupDetailAcitivity.this, "解散群成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailAcitivity.this, "解散群失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });


        } else {
            bt_group_detail.setText("退群");

            bt_group_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                                // 发送退群广播
                                sendExitGroupBroadCastReceiver();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 结束当前页面
                                        finish();

                                        // 提示
                                        Toast.makeText(GroupDetailAcitivity.this, "退群成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }

    // 发送退群广播
    private void sendExitGroupBroadCastReceiver() {
        // 获取广播的管理者对象
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(GroupDetailAcitivity.this);

        Intent intent = new Intent(Constant.EXIT_GROUP);
        // 传递群id
        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

        // 发送退群广播
        mLBM.sendBroadcast(intent);
    }

    private void initView() {
        gv_group_detail = (GridView) findViewById(R.id.gv_group_detail);
        bt_group_detail = (Button) findViewById(R.id.bt_group_detail);
    }
}
