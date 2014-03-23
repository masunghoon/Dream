package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by yuja on 14. 3. 6.
 */
public class TodayGroup implements Comparable<TodayGroup> {

    @SerializedName("date")
    private Date date;
    @SerializedName("todays")
    private List<Today> todayList;

    public TodayGroup() {
    }

    public TodayGroup(Date date) {
        this.date = date;
    }

    public TodayGroup(Date date, List<Today> todayList) {
        this.date = date;
        this.todayList = todayList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Today> getTodayList() {
        return todayList;
    }

    public void setTodayList(List<Today> todayList) {
        this.todayList = todayList;
    }

    @Override
    public int compareTo(TodayGroup another) {
        if(date == null){
            return -1;
        }else if(another.getDate() == null){
            return 1;
        }

        return date.compareTo(another.getDate());
    }
}
