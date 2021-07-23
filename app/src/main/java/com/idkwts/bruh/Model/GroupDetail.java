package com.idkwts.bruh.Model;

public class GroupDetail {
    private String gid;
    private String adminId;
    private String adminName;
    private String groupIcon;
    private String grpMember;

    public GroupDetail(String gid, String adminId, String adminName, String groupIcon, String grpMember) {
        this.gid = gid;
        this.adminId = adminId;
        this.adminName = adminName;
        this.groupIcon = groupIcon;
        this.grpMember = grpMember;
    }

    public GroupDetail()
    {

    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getGrpMember() {
        return grpMember;
    }

    public void setGrpMember(String grpMember) {
        this.grpMember = grpMember;
    }
}
