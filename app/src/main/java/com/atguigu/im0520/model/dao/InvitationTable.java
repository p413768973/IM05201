package com.atguigu.im0520.model.dao;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
// 邀请信息的建表语句
public class InvitationTable {
    public static final String TAB_NAME = "tab_invite";

    public static final String COL_USER_HXID = "user_hxid";
    public static final String COL_USER_NAME = "user_name";

    public static final String COL_GROUP_NAME = "group_name";
    public static final String COL_GROUP_ID = "group_id";

    public static final String COL_REASON = "reason";
    public static final String COL_STATUS = "status";

    public static final String CREATE_TABLE ="create table "
            + TAB_NAME + "("
            + COL_USER_HXID + " text primary key, "
            + COL_USER_NAME + " text, "
            + COL_GROUP_ID + " text, "
            + COL_GROUP_NAME + " text, "
            + COL_REASON + " text, "
            + COL_STATUS + " integer); ";


}
