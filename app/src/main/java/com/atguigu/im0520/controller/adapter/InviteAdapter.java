package com.atguigu.im0520.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.atguigu.im0520.R;
import com.atguigu.im0520.model.bean.GroupInfo;
import com.atguigu.im0520.model.bean.InvitationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/5 0005.
 */
/*
* 任何数据 的变化，都要从以下四个方面考虑
1: 网络数据是否变化
2：数据库、、文件、内存中数据是否变化
3：页面是否需要更新
4：是否需要提示
*/
public class InviteAdapter extends BaseAdapter {
    private Context mContext;
    private List<InvitationInfo> mInvitations = new ArrayList<>();
    private OnInviteListener mOnInviteListener;


    public InviteAdapter(Context context, OnInviteListener onInviteListener) {
        mContext = context;

        // 接受传进来的接口对象
        mOnInviteListener = onInviteListener;
    }

    // 刷新的方法
    public void refresh(List<InvitationInfo> invitationInfos) {

        if (invitationInfos != null && invitationInfos.size() >= 0) {
            // 先清空
            mInvitations.clear();
            // 再添加
            mInvitations.addAll(invitationInfos);
        }

        // 通知刷新
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mInvitations == null ? 0 : mInvitations.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvitations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 创建或获取viewHolder
        ViewHolder holder = null;
        
        if(convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_invite,null);

            // 名称
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_invite_name);
            // 原因
            holder.tvReason = (TextView) convertView.findViewById(R.id.tv_invite_reason);
            // 接受按钮
            holder.btAccept = (Button) convertView.findViewById(R.id.bt_invite_accept);
            // 拒绝按钮
            holder.btReject = (Button) convertView.findViewById(R.id.bt_invite_reject);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前item数据
        final InvitationInfo invitationInfo = mInvitations.get(position);

        // 设置显示
        GroupInfo group = invitationInfo.getGroup();

        if(group == null) {//个人
            // 名称
            holder.tvName.setText(invitationInfo.getUser().getName());

            // 原因
            if(invitationInfo.getStatus() == InvitationInfo.InvitationStatus.NEW_INVITE) {

                // 设置接受和拒绝按钮可见
                holder.btAccept.setVisibility(View.VISIBLE);
                holder.btReject.setVisibility(View.VISIBLE);

                if(invitationInfo.getReason() == null) {
                    holder.tvReason.setText("加个好友吧");
                }else {
                    holder.tvReason.setText(invitationInfo.getReason());
                }

                // 处理button按钮的点击事件
                holder.btAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnInviteListener.onAccept(invitationInfo);
                    }
                });

                // 拒绝按钮的点击事件
                holder.btReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnInviteListener.onReject(invitationInfo);
                    }
                });
            }else if(invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT) {

                // 设置接受和拒绝按钮不可见
                holder.btAccept.setVisibility(View.GONE);
                holder.btReject.setVisibility(View.GONE);

                if(invitationInfo.getReason() == null) {
                    holder.tvReason.setText("接受了邀请");
                }else {
                    holder.tvReason.setText(invitationInfo.getReason());
                }
            }else if(invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER) {

                // 设置接受和拒绝按钮不可见
                holder.btAccept.setVisibility(View.GONE);
                holder.btReject.setVisibility(View.GONE);

                if(invitationInfo.getReason() == null) {
                    holder.tvReason.setText("邀请被接受");
                }else {
                    holder.tvReason.setText(invitationInfo.getReason());
                }
            }
        }else {// 群
            // 显示名称
            holder.tvName.setText(invitationInfo.getGroup().getInvitePerson());

            // 先隐藏按钮
            holder.btAccept.setVisibility(View.GONE);
            holder.btReject.setVisibility(View.GONE);

            // 显示原因
            switch(invitationInfo.getStatus()){

                // 您的群申请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    holder.tvReason.setText("您的群申请已经被接受");
                    break;

                //  您的群邀请已经被接受
                case GROUP_INVITE_ACCEPTED:
                    holder.tvReason.setText("您的群邀请已经被接受");
                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    holder.tvReason.setText("你的群申请已经被拒绝");
                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    holder.tvReason.setText("您的群邀请已经被拒绝");
                    break;

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    // 显示按钮
                    holder.btAccept.setVisibility(View.VISIBLE);
                    holder.btReject.setVisibility(View.VISIBLE);

                    // 群邀请状态的接受按钮
                    holder.btAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteAccept(invitationInfo);
                        }
                    });

                    // 群邀请状态的拒绝按钮
                    holder.btReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteReject(invitationInfo);
                        }
                    });

                    holder.tvReason.setText("您收到了群邀请");
                    break;

                // 您收到了群申请
                case NEW_GROUP_APPLICATION:
                    // 显示按钮
                    holder.btAccept.setVisibility(View.VISIBLE);
                    holder.btReject.setVisibility(View.VISIBLE);

                    // 群申请状态的接受按钮
                    holder.btAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationAccept(invitationInfo);
                        }
                    });

                    // 群申请状态的拒绝按钮
                    holder.btReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationReject(invitationInfo);
                        }
                    });

                    holder.tvReason.setText("您收到了群申请");
                    break;

                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    holder.tvReason.setText("你接受了群邀请");
                    break;

                // 您批准了群加入
                case GROUPO_ACCEPT_APPLICATION:
                    holder.tvReason.setText("您批准了群加入");
                    break;
            }
        }

        // 返回convertView
        return convertView;
    }

    static class ViewHolder{
        TextView tvName;        //名称
        TextView tvReason;      // 原因

        Button btAccept;        // 接受按钮
        Button btReject;        // 拒绝按钮
    }

    public interface OnInviteListener{
        // 接受按钮的点击事件
        void onAccept(InvitationInfo invitationInfo);
        // 拒绝按钮的点击事件
        void onReject(InvitationInfo invitationInfo);

        // 邀请信息接受按钮点击事件
        void onInviteAccept(InvitationInfo invitationInfo);
        // 邀请信息拒绝按钮点击事件
        void onInviteReject(InvitationInfo invitationInfo);

        // 申请信息接受按钮点击事件
        void onApplicationAccept(InvitationInfo invitationInfo);

        // 申请信息拒绝按钮点击事件
        void onApplicationReject(InvitationInfo invitationInfo);
    }

}
