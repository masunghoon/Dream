package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by yuja on 14. 1. 26.
 */
public class Plan extends Bucket implements Comparable<Plan> {

    @SerializedName("bucket_id")
    private Integer bucketId;

    @SerializedName("isDone")
    private Boolean isDone;

    @SerializedName("date")
    private Date date;

    public Integer getBucketId() {
        return bucketId;
    }

    public void setBucketId(Integer bucketId) {
        this.bucketId = bucketId;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(Plan plan) {
        return date.compareTo(plan.getDate());
    }

    @Override
    public String toString() {
        return "Plan{" +
                "bucketId=" + bucketId +
                ", isDone=" + isDone +
                ", date=" + date +
                '}';
    }
}
