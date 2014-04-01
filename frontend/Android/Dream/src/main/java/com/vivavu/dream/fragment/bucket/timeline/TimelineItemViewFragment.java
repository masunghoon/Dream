package com.vivavu.dream.fragment.bucket.timeline;

import android.os.Bundle;

import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.bucket.timeline.Post;

/**
 * Created by yuja on 2014-03-28.
 */
public class TimelineItemViewFragment extends CustomBaseFragment {
    public static final String TAG = "com.vivavu.dream.fragment.bucket.timeline.TimelineItemViewFragment";

    private Post post;

    public TimelineItemViewFragment(Post post) {
        this.post = post;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
