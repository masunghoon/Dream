package com.vivavu.dream.model.bucket;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by yuja on 14. 1. 15.
 */
public class BucketWrapped {
    @JsonProperty("bucket")
    private Bucket bucket;

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }
}
