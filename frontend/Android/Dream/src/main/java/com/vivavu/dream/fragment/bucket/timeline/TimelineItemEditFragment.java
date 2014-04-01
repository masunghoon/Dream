package com.vivavu.dream.fragment.bucket.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.util.DateUtils;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-28.
 */
public class TimelineItemEditFragment extends CustomBaseFragment{

    public static final String TAG = "com.vivavu.dream.fragment.bucket.timeline.TimelineItemEditFragment";

    protected Post post;
    @InjectView(R.id.container_post_info)
    LinearLayout mContainerPostInfo;
    @InjectView(R.id.txt_post_text)
    EditText mTxtPostText;
    @InjectView(R.id.txt_post_date)
    TextView mTxtPostDate;
    @InjectView(R.id.btn_post_camera)
    Button mBtnPostCamera;
    @InjectView(R.id.btn_post_etc)
    Button mBtnPostEtc;
    @InjectView(R.id.btn_post_facebook)
    Button mBtnPostFacebook;


    public TimelineItemEditFragment(Post post) {
        this.post = post;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_timeline_item_edit, container, false);
        ButterKnife.inject(this, rootView);

        buinData(post);
        initEvent();
        return rootView;
    }

    private void initEvent() {
        mTxtPostText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //날짜 달력을 선택하는 것으로 옮길것
            }
        });

    }

    private void buinData(Post post) {
        mTxtPostText.setText(post.getText());
        mTxtPostDate.setText(DateUtils.getDateString(post.getTimestamp(), "yyyy-MM-dd hh:mm"));
    }

    public Post getPost() {
        post.setText(String.valueOf(mTxtPostText.getText()));
        post.setTimestamp(DateUtils.getDateFromString(String.valueOf(mTxtPostDate.getText()), "yyyy-MM-dd hh:mm", new Date()));
        return post;
    }
}
