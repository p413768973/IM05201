package com.atguigu.im0520.model;

import android.content.Context;

import com.atguigu.im0520.model.bean.UserInfo;
import com.atguigu.im0520.model.dao.UserAccountDao;
import com.atguigu.im0520.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/8/2 0002.
 */
// 模型层类
public class Modle {
    private Context mContext;

    private UserAccountDao userAccountDao;
    private DBManager mDbManager;

    // 私有化构造
    private Modle() {

    }

    private static Modle instance = new Modle();

    public static Modle getInstance(){
        return instance;
    }

    public void init(Context context){
        mContext = context;

        userAccountDao = new UserAccountDao(context);

        EventListener eventListener = new EventListener(mContext);
    }

    // 获取用户账号的操作类
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }

    // 创建全局线程池
    private ExecutorService executorService = Executors.newCachedThreadPool();

    // 获取全局线程池对象
    public ExecutorService getGlobalThreadPool(){
        return executorService;
    }

    // 登录成功后的处理方法
    public void loginSuccess(UserInfo account){

        if(account == null) {
            return;
        }

        if(mDbManager != null) {
            mDbManager.close();
        }

        // 创建数据库的管理者
        mDbManager = new DBManager(mContext, account.getName());
    }

    // 获取数据库管理者对象
    public DBManager getmDbManager() {
        return mDbManager;
    }
}
