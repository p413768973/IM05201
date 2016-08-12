package com.atguigu.im0520.model.db;

import android.content.Context;

import com.atguigu.im0520.model.dao.ContactDao;
import com.atguigu.im0520.model.dao.InvitationDao;

/**
 * Created by Administrator on 2016/8/5 0005.
 */
public class DBManager {

    private final DBHelper mDbHelper;
    private final ContactDao contactDao;
    private final InvitationDao invitationDao;

    public DBManager(Context context, String name) {

        mDbHelper = new DBHelper(context, name);

        contactDao = new ContactDao(mDbHelper);

        invitationDao = new InvitationDao(mDbHelper);
    }

    // 获取联系人操作类
    public ContactDao getContactDao(){
        return contactDao;
    }

    // 获取邀请信息的操作类
    public InvitationDao getInvitationDao() {
        return invitationDao;
    }

    public void close() {
        mDbHelper.close();
    }
}
