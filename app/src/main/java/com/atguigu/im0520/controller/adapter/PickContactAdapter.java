package com.atguigu.im0520.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.atguigu.im0520.R;
import com.atguigu.im0520.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/8 0008.
 */
public class PickContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<PickContactInfo> mPickContacts = new ArrayList<>();
    private List<String> mExistingMembers = new ArrayList<>();

    public PickContactAdapter(Context context, List<PickContactInfo> pickContacts, List<String> existingMembers) {
        mContext = context;

        if (pickContacts != null && pickContacts.size() >= 0) {
            mPickContacts.clear();

            mPickContacts.addAll(pickContacts);
        }

        // 获取群中已经存在的成员集合
        if(existingMembers != null && existingMembers.size() >= 0) {
            mExistingMembers.clear();

            mExistingMembers.addAll(existingMembers);
        }
    }

    // 获取选中的联系人
    public List<String> getAddMembers() {

        // 准备一个返回数据的集合
        List<String> members = new ArrayList<>();

        for (PickContactInfo contact : mPickContacts) {

            // 查找到已经选中的联系人
            if(contact.isChecked()) {
                members.add(contact.getUser().getName());
            }
        }

        return members;
    }

    @Override
    public int getCount() {
        return mPickContacts == null ? 0 : mPickContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return mPickContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_pick_contact, null);

            holder.tvName = (TextView) convertView.findViewById(R.id.tv_pick_contact_name);
            holder.isChecked = (CheckBox) convertView.findViewById(R.id.cb_pick_contact);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 2 获取当前item数据
        PickContactInfo pickContactInfo = mPickContacts.get(position);

        // 3 设置显示数据
        holder.tvName.setText(pickContactInfo.getUser().getName());
        holder.isChecked.setChecked(pickContactInfo.isChecked());

        // 当前item的联系人已经在该群中
        if(mExistingMembers.contains(pickContactInfo.getUser().getHxid())) {
            // 更改显示
            holder.isChecked.setChecked(true);
            // 更改数据
            pickContactInfo.setIsChecked(true);
        }

        // 4 返回convertView
        return convertView;
    }

    static class ViewHolder {
        TextView tvName;            // 名称
        CheckBox isChecked;         // 是否选中
    }
}
