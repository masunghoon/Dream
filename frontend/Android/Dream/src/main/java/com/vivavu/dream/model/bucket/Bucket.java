package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.vivavu.dream.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yuja on 14. 1. 9.
 */
@DatabaseTable(tableName = "buckets")
public class Bucket implements Serializable{
    @DatabaseField(id = true)
    @SerializedName("id")
    private Integer id;

    @DatabaseField
    @SerializedName("title")
    private String title;

    @DatabaseField
    @SerializedName("description")
    private String description;

    @DatabaseField
    @SerializedName("user_id")
    private Integer userId;

    @DatabaseField
    @SerializedName("deadline")
    private Date deadline;

    @DatabaseField
    @SerializedName("status")
    private Integer status;

    @DatabaseField
    @SerializedName("is_private")
    private Integer isPrivate;

    @DatabaseField
    @SerializedName("scope")
    private String scope;

    @DatabaseField
    @SerializedName("range")
    private String range;

    @DatabaseField
    @SerializedName("rpt_type")
    private String rptType;

    @DatabaseField
    @SerializedName("rpt_cndt")
    private String rptCndt;

    @DatabaseField
    @SerializedName("reg_dt")
    private Date regDate;

    @DatabaseField
    @SerializedName("lst_mod_dt")
    private Date lastModDate;

    @DatabaseField(defaultValue = "0")
    private int modFlag;

    public Bucket(){

    }

    public Bucket(String title, Date deadline) {
        this.deadline = deadline;
        this.title = title;
    }


    public String getRemainDays(){
        return DateUtils.getRemainDayInString(getDeadline());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getRptType() {
        return rptType;
    }

    public void setRptType(String rptType) {
        this.rptType = rptType;
    }

    public String getRptCndt() {
        return rptCndt;
    }

    public void setRptCndt(String rptCndt) {
        this.rptCndt = rptCndt;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(Date lastModDate) {
        this.lastModDate = lastModDate;
    }

    public int getModFlag() {
        return modFlag;
    }

    public void setModFlag(int modFlag) {
        this.modFlag = modFlag;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userId=" + userId +
                ", deadline=" + deadline +
                ", status=" + status +
                ", isPrivate=" + isPrivate +
                ", scope='" + scope + '\'' +
                ", range='" + range + '\'' +
                ", rptType='" + rptType + '\'' +
                ", rptCndt='" + rptCndt + '\'' +
                ", regDate=" + regDate +
                ", lastModDate=" + lastModDate +
                ", modFlag=" + modFlag +
                '}';
    }
}
