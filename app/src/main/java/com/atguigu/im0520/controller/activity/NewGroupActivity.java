package com.atguigu.im0520.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.atguigu.im0520.R;
import com.atguigu.im0520.model.Modle;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

// 新建群组
public class NewGroupActivity extends Activity {
    private EditText et_new_group_name;
    private EditText et_new_group_desc;
    private CheckBox cb_new_group_public;
    private CheckBox cb_new_group_invite;
    private Button bt_new_group_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_group);

        initView();

        initListener();
    }

    private void initListener() {
        // 创建按钮点击事件
        bt_new_group_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到选择联系人页面
                Intent intent = new Intent(NewGroupActivity.this, PickContactAcitivity.class);
                intent.putExtra("1",100);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            creatGroup(data.getExtras().getStringArray("members"));
        }
    }

    // 创建群
    private void creatGroup(final String[] memberses) {
        // 获取群名称
        final String groupName = et_new_group_name.getText().toString();
        // 获取群描述
        final String groupDesc = et_new_group_desc.getText().toString();

        // 联网
        // 去环信服务器创建群
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMGroupManager.EMGroupOptions emOptions = new EMGroupManager.EMGroupOptions();
                emOptions.maxUsers = 200;// 群最大人数
                EMGroupManager.EMGroupStyle groupStyle = null;

                if(cb_new_group_public.isChecked()) {
                    if(cb_new_group_invite.isChecked()) {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                }else {
                    if(cb_new_group_invite.isChecked()) {
                        groupStyle  = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }
                emOptions.style = groupStyle;

                try {
                    // 群名称  群描述  群成员  群创建原因 群参数设置
                    EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, memberses, "创建群", emOptions);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();

                            // 结束当前页
                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        et_new_group_name = (EditText) findViewById(R.id.et_new_group_name);
        et_new_group_desc = (EditText) findViewById(R.id.et_new_group_desc);
        cb_new_group_public = (CheckBox) findViewById(R.id.cb_new_group_public);
        cb_new_group_invite = (CheckBox) findViewById(R.id.cb_new_group_invite);
        bt_new_group_create = (Button) findViewById(R.id.bt_new_group_create);
    }
}
