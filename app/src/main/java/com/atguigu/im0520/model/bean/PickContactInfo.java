package com.atguigu.im0520.model.bean;

/**
 * Created by Administrator on 2016/8/8 0008.
 */
// 选择联系人bean对象
public class PickContactInfo {
    private UserInfo user;
    private boolean isChecked;

    public PickContactInfo(UserInfo user, boolean isChecked) {
        this.user = user;
        this.isChecked = isChecked;
    }

    public PickContactInfo() {
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
