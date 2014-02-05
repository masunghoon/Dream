package com.vivavu.dream.adapter.bucket;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.BucketViewActivity;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.util.DateUtils;

import java.util.List;

/**
 * Created by yuja on 14. 1. 9.
 */
public class BucketAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater mInflater;
    private List<Bucket> buckets;
    private Context _context;
    private int resource;

    public BucketAdapter(Context context, int resource, List<Bucket> buckets) {
        super();
        this._context = context;
        this.resource = resource;
        this.mInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.buckets = buckets;
    }

    @Override
    public int getCount() {
        if(buckets != null){
            return buckets.size();
        }
        return 0;
    }

    @Override
    public Bucket getItem(int i) {
        return buckets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int res = R.layout.bucket_list_item;
        Bucket item = buckets.get(i);
        if(view == null){
            view = mInflater.inflate(res, viewGroup, false);
        }

        Button btnDone  = (Button) view.findViewById(R.id.bucket_btn_done);
        btnDone.setOnClickListener(this);
        btnDone.setEnabled(false);
        if(item.getIsLive() == 1){
            btnDone.setSelected(true);
        }else {
            btnDone.setSelected(false);
        }

        view.setOnClickListener(this);
        TextView title = (TextView) view.findViewById(R.id.bucket_item_title);
        title.setOnClickListener(this);
        title.setText(item.getTitle());
        title.setTag(item.getId());
        TextView scope = (TextView) view.findViewById(R.id.bucket_item_scope);
        scope.setText(item.getRange());
        TextView remain = (TextView) view.findViewById(R.id.bucket_item_remain);
        remain.setText("remain "+DateUtils.getRemainDay(item.getDeadline()).toString() + " Days" );

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.bucket_item_progressbar);
        if(item.getTodos() == null || item.getTodos().size() < 1){
            progressBar.setVisibility(ProgressBar.GONE);
        }else{
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setProgress(item.getProgress());
        }
        progressBar.setEnabled(true);

        view.setTag(item);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bucket_item_title){
            Intent intent = new Intent(getContext().getApplicationContext(), BucketViewActivity.class);
            intent.putExtra("bucketId", (Integer) view.getTag());
            getContext().startActivity(intent);
        }
    }

    private Context getContext() {
        return this._context;
    }

    public void notifyDataSetChanged(List<Bucket> buckets){
        this.buckets.clear();
        this.buckets.addAll(buckets);

        super.notifyDataSetChanged();
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }


}
