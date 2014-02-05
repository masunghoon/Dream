package com.vivavu.dream.model.bucket;

import com.vivavu.dream.util.JsonDateDeserializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yuja on 14. 1. 9.
 */
public class Todo implements Serializable{
    @JsonDeserialize(using= JsonDateDeserializer.class)
    @JsonProperty("todoDeadline")
    private Date todoDeadLine;
    @JsonProperty("todoID")
    private Integer todoId;
    @JsonProperty("todoIsLive")
    private Integer todoIsLive;
    @JsonProperty("todoIsPrivate")
    private Integer todoIsPrivate;
    @JsonProperty("todoParent_id")
    private Integer todoParentId;
    @JsonProperty("todoRange")
    private String todoRange;
    @JsonDeserialize(using= JsonDateDeserializer.class)
    @JsonProperty("todoRegDate")
    private Date todoRegDate;
    @JsonProperty("todoScope")
    private String todoScope;
    @JsonProperty("todoTitle")
    private String todoTitle;

    @JsonProperty("todoRptCndt")
    private String todoRptCndt;

    @JsonProperty("todoRptType")
    private String todoRptType;

    public Date getTodoDeadLine() {
        return todoDeadLine;
    }

    public void setTodoDeadLine(Date todoDeadLine) {
        this.todoDeadLine = todoDeadLine;
    }

    public Integer getTodoId() {
        return todoId;
    }

    public void setTodoId(Integer todoId) {
        this.todoId = todoId;
    }

    public Integer getTodoIsLive() {
        return todoIsLive;
    }

    public void setTodoIsLive(Integer todoIsLive) {
        this.todoIsLive = todoIsLive;
    }

    public Integer getTodoIsPrivate() {
        return todoIsPrivate;
    }

    public void setTodoIsPrivate(Integer todoIsPrivate) {
        this.todoIsPrivate = todoIsPrivate;
    }

    public Integer getTodoParentId() {
        return todoParentId;
    }

    public void setTodoParentId(Integer todoParentId) {
        this.todoParentId = todoParentId;
    }

    public String getTodoRange() {
        return todoRange;
    }

    public void setTodoRange(String todoRange) {
        this.todoRange = todoRange;
    }

    public Date getTodoRegDate() {
        return todoRegDate;
    }

    public void setTodoRegDate(Date todoRegDate) {
        this.todoRegDate = todoRegDate;
    }

    public String getTodoScope() {
        return todoScope;
    }

    public void setTodoScope(String todoScope) {
        this.todoScope = todoScope;
    }

    public String getTodoTitle() {
        return todoTitle;
    }

    public void setTodoTitle(String todoTitle) {
        this.todoTitle = todoTitle;
    }

    public String getTodoRptCndt() {
        return todoRptCndt;
    }

    public void setTodoRptCndt(String todoRptCndt) {
        this.todoRptCndt = todoRptCndt;
    }

    public String getTodoRptType() {
        return todoRptType;
    }

    public void setTodoRptType(String todoRptType) {
        this.todoRptType = todoRptType;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "todoDeadLine=" + todoDeadLine +
                ", todoId=" + todoId +
                ", todoIsLive=" + todoIsLive +
                ", todoIsPrivate=" + todoIsPrivate +
                ", todoParentId=" + todoParentId +
                ", todoRange='" + todoRange + '\'' +
                ", todoRegDate=" + todoRegDate +
                ", todoScope='" + todoScope + '\'' +
                ", todoTitle='" + todoTitle + '\'' +
                '}';
    }
}
