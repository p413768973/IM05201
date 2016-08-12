package com.atguigu.im0520.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.atguigu.im0520.model.bean.UserInfo;
import com.atguigu.im0520.model.db.UserAccountDB;

/**
 * Created by Administrator on 2016/8/2 0002.
 */
public class UserAccountDao {

    private final UserAccountDB mHelper;

    public UserAccountDao(Context context) {
        mHelper = new UserAccountDB(context);
    }

    // 添加用户到数据库
    public void addAccount(UserInfo user){
        // 获取数据库链接
        SQLiteDatabase db = mHelper.getWritableDatabase();

        // 添加用户
        ContentValues values = new ContentValues();
        values.put(UserAccountTable.COL_NAME, user.getName());
        values.put(UserAccountTable.COL_HXID, user.getHxid());
        values.put(UserAccountTable.COL_NICK, user.getNick());
        values.put(UserAccountTable.COL_PHOTO, user.getPhoto());

        db.replace(UserAccountTable.TAB_NAME,null,values);// ctrl+alt+/
    }

    // 获取用户
    public UserInfo getAccount(String name){
        //  获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        // 执行查询语句

        String sql = "select * from "+ UserAccountTable.TAB_NAME+" where "+ UserAccountTable.COL_NAME +" =?";
        Cursor cursor = db.rawQuery(sql, new String[]{name});
        UserInfo userInfo = null;
        if(cursor.moveToNext()) {
            userInfo = new UserInfo();

            // 封装对象
            userInfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }
        // 关闭cursor
        cursor.close();

        // 返回资源
        return userInfo;
    }

    // 根据环信id获取所有用户信息
    public UserInfo getAccountByHxId(String hxId){
        //  获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        // 执行查询语句

        String sql = "select * from "+ UserAccountTable.TAB_NAME+" where "+ UserAccountTable.COL_HXID +" =?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});
        UserInfo userInfo = null;
        if(cursor.moveToNext()) {
            userInfo = new UserInfo();

            // 封装对象
            userInfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }
        // 关闭cursor
        cursor.close();

        // 返回资源
        return userInfo;
    }

}
