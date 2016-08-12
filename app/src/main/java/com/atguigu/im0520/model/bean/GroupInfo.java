package com.atguigu.im0520.model.bean;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
public class GroupInfo {// alt +shift +S
    private String groupName;   // 群名称
    private String groupId;     // 群的环信id
    private String invitePerson;    // 邀请人的信息

    public GroupInfo() {
    }

    public GroupInfo(String groupName, String groupId, String invitePerson) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.invitePerson = invitePerson;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getInvitePerson() {
        return invitePerson;
    }

    public void setInvitePerson(String invitePerson) {
        this.invitePerson = invitePerson;
    }
}
