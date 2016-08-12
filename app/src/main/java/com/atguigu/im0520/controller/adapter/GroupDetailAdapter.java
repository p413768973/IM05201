package com.atguigu.im0520.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.im0520.R;
import com.atguigu.im0520.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/10 0010.
 */
public class GroupDetailAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserInfo> mUsers = new ArrayList<>();
    private boolean mIsCanModify;   // 是否能修改 true:能修改；false:不能修改
    private boolean mIsDeleteModel;// 删除模式 true:可以删除；false：不可以删除
    private OnGroupDetailListener mOnGroupDetailListener;// 设置全局接口

    public GroupDetailAdapter(Context context, boolean isCanModify, OnGroupDetailListener onGroupDetailListener) {
        mContext = context;
        mIsCanModify = isCanModify;

//        initUsers();

        // 获取接口实现类
        mOnGroupDetailListener = onGroupDetailListener;
    }

    private void initUsers() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");

        mUsers.add(delete);
        mUsers.add(0, add);
    }

    // 获取当前删除模式状态
    public boolean ismIsDeleteModel() {
        return mIsDeleteModel;
    }

    // 设置删除模式
    public void setmIsDeleteModel(boolean mIsDeleteModel) {
        this.mIsDeleteModel = mIsDeleteModel;
    }

    // 刷新方法
    public void refresh(List<UserInfo> users) {

        if (users != null && users.size() >= 0) {

            mUsers.clear();

            initUsers();

            mUsers.addAll(0, users);
        }

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 获取或创建viewHolder
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_group_detail, null);

            // 获取对象
            holder.name = (TextView) convertView.findViewById(R.id.tv_group_detail_name);
            holder.delete = (ImageView) convertView.findViewById(R.id.iv_group_detail_delete);
            holder.photo = (ImageView) convertView.findViewById(R.id.iv_group_detail_photo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();



        }


        // 获取当前item对象

        // 设置显示数据
        // 判断是否可以修改
        if (mIsCanModify) {// 可以修改

            //处理布局显示
            if (position == getCount() - 1) {// 减号-

                // 判断是否是删除模式
                if(mIsDeleteModel) {
                    convertView.setVisibility(View.INVISIBLE);
                }else {
                    convertView.setVisibility(View.VISIBLE);

                    // 设置数据显示
                    holder.photo.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    holder.name.setVisibility(View.INVISIBLE);
                    holder.delete.setVisibility(View.GONE);
                }

            } else if (position == getCount() - 2) {//加号+
                // 判断是否是删除模式
                if(mIsDeleteModel) {
                    convertView.setVisibility(View.INVISIBLE);
                }else {
                    convertView.setVisibility(View.VISIBLE);

                    // 设置数据显示
                    holder.photo.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    holder.name.setVisibility(View.INVISIBLE);
                    holder.delete.setVisibility(View.GONE);
                }
            }else {// 正常的
                convertView.setVisibility(View.VISIBLE);

                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(mUsers.get(position).getName());

                holder.photo.setImageResource(R.drawable.atguigu_logo);
                
                // 删除模式判断
                if(mIsDeleteModel) {
                    holder.delete.setVisibility(View.VISIBLE);
                }else {
                    holder.delete.setVisibility(View.GONE);
                }
            }

            // 点击事件处理
            if(position == getCount() - 1) {// 减号
                // 减号的点击事件
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 从非删除模式切换到删除模式
                        if(!mIsDeleteModel) {
                            mIsDeleteModel = true;

                            notifyDataSetChanged();
                        }
                    }
                });
            }else if(position == getCount() - 2) {// 加号

                // 加号点击事件处理
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            }else {// 正常

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onDeleteMember(mUsers.get(position));
                    }
                });
            }
        } else {// 不可以修改
            if (position == getCount() - 1 || position == getCount() - 2) {
                // 直接隐藏布局
                convertView.setVisibility(View.GONE);
            } else {
                convertView.setVisibility(View.VISIBLE);

                // 获取当前item数据
                UserInfo userInfo = mUsers.get(position);

                // 设置数据
                holder.name.setText(userInfo.getName());
                holder.photo.setImageResource(R.drawable.atguigu_logo);
                holder.delete.setVisibility(View.GONE);
            }
        }

        // 返回convertveiw
        return convertView;
    }

    static class ViewHolder {
        TextView name;      // 名称
        ImageView photo;    // 头像
        ImageView delete;   // 删除图标
    }

    public interface OnGroupDetailListener{

        // 添加群成员
        void onAddMembers();

        // 删除群成员
        void onDeleteMember(UserInfo user);
    }
}
