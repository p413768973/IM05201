package com.atguigu.im0520.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.im0520.R;
import com.atguigu.im0520.controller.adapter.PickContactAdapter;
import com.atguigu.im0520.model.Modle;
import com.atguigu.im0520.model.bean.PickContactInfo;
import com.atguigu.im0520.model.bean.UserInfo;
import com.atguigu.im0520.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

// 选择联系人页面
public class PickContactAcitivity extends Activity {
    private TextView tv_pick_contact_save;
    private ListView lv_pick_contact;
    private PickContactAdapter mPickContactAdapter;
    private List<PickContactInfo> mPickContacts;
    private List<String> mExistingMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pick_contact_acitivity);
        if (getIntent().getIntExtra("1", 2) != 100) {
            getData();
        }


        initView();

        initData();

        initListener();
    }

    //     获取传递过来的参数
    private void getData() {
        String groupId = getIntent().getExtras().getString(Constant.GROUP_ID);

        if (groupId != null) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            // 获取群成员的环信id
            mExistingMembers = group.getMembers();
        }

        if (mExistingMembers == null) {
            mExistingMembers = new ArrayList<>();
        }
    }

    private void initListener() {
        lv_pick_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 获取checkbox对象
                CheckBox cb_pick_contact = (CheckBox) view.findViewById(R.id.cb_pick_contact);
                cb_pick_contact.setChecked(!cb_pick_contact.isChecked());

                // 改变内存集合中的数据状态
                PickContactInfo pickContactInfo = mPickContacts.get(position);
                pickContactInfo.setIsChecked(cb_pick_contact.isChecked());

                // 刷新页面
                mPickContactAdapter.notifyDataSetChanged();
            }
        });

        // 保存按钮的点击事件
        tv_pick_contact_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取选中的联系人
                List<String> members = mPickContactAdapter.getAddMembers();

                // 返回
                Intent intent = new Intent();

                intent.putExtra("members", members.toArray(new String[0]));

                setResult(RESULT_OK, intent);

                // 结束当前页
                finish();
            }
        });
    }

    private void initData() {
        // 创建适配器

        // 从本地数据库中获取所有联系人
        List<UserInfo> contacts = Modle.getInstance().getmDbManager().getContactDao().getContacts();

        // 需要用于显示的联系人集合
        mPickContacts = new ArrayList<>();

        // 联系人数据转换
        if (contacts != null && contacts.size() >= 0) {
            for (UserInfo contact : contacts) {
                PickContactInfo pickContactInfo = new PickContactInfo(contact, false);

                mPickContacts.add(pickContactInfo);
            }
        }

        mPickContactAdapter = new PickContactAdapter(PickContactAcitivity.this, mPickContacts, mExistingMembers);
        // 添加适配器到listview中
        lv_pick_contact.setAdapter(mPickContactAdapter);

    }

    private void initView() {
        tv_pick_contact_save = (TextView) findViewById(R.id.tv_pick_contact_save);
        lv_pick_contact = (ListView) findViewById(R.id.lv_pick_contact);
    }
}
