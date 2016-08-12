package com.atguigu.im0520.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.atguigu.im0520.model.dao.UserAccountTable;

/**
 * Created by Administrator on 2016/8/2 0002.
 */
public class UserAccountDB extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;

    public UserAccountDB(Context context) {
        super(context, "account.db", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table user (col_name text primary key, col_hxid text);");
        db.execSQL(UserAccountTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
