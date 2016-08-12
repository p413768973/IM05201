package com.atguigu.im0520.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.atguigu.im0520.R;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/8 0008.
 */
public class GroupListAdapter extends BaseAdapter {
    private Context mContext;
    private List<EMGroup> mGroups = new ArrayList<>();

    public GroupListAdapter(Context context) {
        mContext = context;
    }

    // 刷新方法，提供数据
    public void refresh(List<EMGroup> groups) {

        // 校验
        if (groups != null && groups.size() >= 0) {
            mGroups.clear();
            mGroups.addAll(groups);
        }

        // 通知刷新
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 创建或获取viewHolder
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_group_list, null);

            holder.tvName = (TextView) convertView.findViewById(R.id.tv_group_list_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前item数据
        EMGroup emGroup = mGroups.get(position);

        // 数据显示
        holder.tvName.setText(emGroup.getGroupName());

        // 返回conconvertView

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
    }
}
