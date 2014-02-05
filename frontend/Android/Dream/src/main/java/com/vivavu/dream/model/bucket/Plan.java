package com.vivavu.dream.model.bucket;

import com.vivavu.dream.util.JsonPlanDateDeserializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.Date;

/**
 * Created by yuja on 14. 1. 26.
 */
public class Plan extends Bucket implements Comparable<Plan> {

    @JsonProperty("bucket_id")
    private Integer bucketId;

    @JsonProperty("isDone")
    private Boolean isDone;

    @JsonDeserialize(using= JsonPlanDateDeserializer.class)
    @JsonProperty("date")
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
