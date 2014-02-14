package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuja on 14. 1. 15.
 */
public class BucketWrapped {
    @SerializedName("bucket")
    private Bucket bucket;

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }
}
