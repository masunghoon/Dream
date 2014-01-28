package com.vivavu.dream.model.bucket;

import com.vivavu.dream.util.DateUtils;

import java.util.Date;

/**
 * Created by yuja on 14. 1. 14.
 */
public class Dday {
    private String range;
    private Date deadline;

    public Dday(String range, Date deadline) {
        this.range = range;
        this.deadline = deadline;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getDdayString(){
        return DateUtils.getDateString(this.deadline, "yyyy-MM-dd");
    }
    @Override
    public String toString() {
        return "Dday{" +
                "range='" + range + '\'' +
                ", deadline=" + deadline +
                '}';
    }
}
