package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;
import com.vivavu.dream.util.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by yuja on 14. 1. 9.
 */
public class Bucket implements Serializable{
    @SerializedName("deadline")
    private Date deadline;
    @SerializedName("description")
    private String description;
    @SerializedName("id")
    private Integer id;
    @SerializedName("is_live")
    private Integer isLive;
    @SerializedName("is_private")
    private Integer isPrivate;
    @SerializedName("level")
    private Integer level = 0;
    @SerializedName("parent_id")
    private Integer parentId;
    @SerializedName("range")
    private String range;

    @SerializedName("reg_date")
    private Date regDate;

    @SerializedName("rptType")
    private String rptType;
    @SerializedName("rptCndt")
    private String rptCndt;

    @SerializedName("scope")
    private String scope;
    @SerializedName("title")
    private String title;
    @SerializedName("user_id")
    private Integer userId;
    @SerializedName("todos")
    private List<Todo> todos;

    @SerializedName("subBuckets")
    private List<Bucket> subBuckets;

    @SerializedName("uri")
    private String uri;

    public Bucket(){

    }

    public Bucket(String title, Date deadline) {
        this.deadline = deadline;
        this.title = title;
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsLive() {
        return isLive;
    }

    public void setIsLive(Integer isLive) {
        this.isLive = isLive;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getParentId() {
        return parentId;
    }

    public Integer getParentID() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public int getProgress(){
        int total = getTodos().size();
        int count = 0;
        if(total < 1){
            return 0;
        }

        for(int i = 0 ; i < total ; i++){
            Todo todo = getTodos().get(i);
            if(todo.getTodoIsLive()==1){
                count++;
            }
        }
        double p = count/(double)total *100;

        return (int) p;
    }

    public List<Bucket> getSubBuckets() {
        return subBuckets;
    }

    public void setSubBuckets(List<Bucket> subBuckets) {
        this.subBuckets = subBuckets;
    }

    public String getRemainDays(){
        return DateUtils.getRemainDayInString(getDeadline());
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "deadline=" + deadline +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", isLive=" + isLive +
                ", isPrivate=" + isPrivate +
                ", level='" + level + '\'' +
                ", parentId=" + parentId +
                ", range='" + range + '\'' +
                ", regDate=" + regDate +
                ", rptType='" + rptType + '\'' +
                ", rptCndt='" + rptCndt + '\'' +
                ", scope='" + scope + '\'' +
                ", title='" + title + '\'' +
                ", userId=" + userId +
                ", todos=" + todos +
                ", subBuckets=" + subBuckets +
                ", uri='" + uri + '\'' +
                '}';
    }
}
