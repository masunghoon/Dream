package com.vivavu.dream.model.bucket;

import com.vivavu.dream.util.JsonDateDeserializer;
import com.vivavu.dream.util.JsonDateSerializer;
import com.vivavu.dream.util.JsonDeadlineDateSerializer;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by yuja on 14. 1. 9.
 */
public class Bucket implements Serializable{
    @JsonDeserialize(using= JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDeadlineDateSerializer.class)
    @JsonProperty("deadline")
    private Date deadline;
    @JsonProperty("description")
    private String description;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("is_live")
    private Integer isLive;
    @JsonProperty("is_private")
    private Integer isPrivate;
    @JsonProperty("level")
    private Integer level = 0;
    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("range")
    private String range;

    @JsonDeserialize(using= JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonProperty("reg_date")
    private Date regDate;

    @JsonProperty("rptType")
    private String rptType;
    @JsonProperty("rptCndt")
    private String rptCndt;

    @JsonProperty("scope")
    private String scope;
    @JsonProperty("title")
    private String title;
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("todos")
    private List<Todo> todos;

    @JsonProperty("subBuckets")
    private List<Bucket> subBuckets;

    @JsonProperty("uri")
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

    @JsonIgnore
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
