package com.vivavu.dream.fragment.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketAddActivity;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.task.CustomAsyncTask;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.util.FileUtils;
import com.vivavu.dream.util.ImageUtil;
import com.vivavu.dream.view.CustomPopupWindow;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 28.
 */
public class MainShelfItemFragment extends CustomBaseFragment{
    @InjectView(R.id.btn_book_write)
    Button mBtnBookWrite;
    @InjectView(R.id.btn_book_setting)
    Button mBtnBookSetting;
    @InjectView(R.id.book_title)
    TextView mBookTitle;
    @InjectView(R.id.book_dudate)
    TextView mBookDudate;
    @InjectView(R.id.book_status)
    TextView mBookStatus;
    @InjectView(R.id.book_cover)
    LinearLayout mBookCover;
    @InjectView(R.id.book_cover_image)
    ImageView mBookCoverImage;
    private Bucket bucket;

    View popupView;
    CustomPopupWindow mPopupWindow;

    public MainShelfItemFragment() {
        this.bucket = new Bucket();
    }

    public static MainShelfItemFragment newInstance(Bucket bucket) {
        return new MainShelfItemFragment(bucket);
    }

    public MainShelfItemFragment(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.shelf_item, container, false);
        ButterKnife.inject(this, viewGroup);
        viewGroup.setBackgroundColor(Color.argb(127, 0, 0, 255));
        init();
        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bucket.getCvrImgUrl() != null) {
            ImageDownloadTask imageDownloadTask = new ImageDownloadTask();
            imageDownloadTask.execute(bucket.getCvrImgUrl());
        }
    }

    public void init() {

        mBookTitle.setText(bucket.getTitle());
        mBookDudate.setText(DateUtils.getDateString(bucket.getDeadline(), "yyyy-MM-dd"));
        mBtnBookWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent();
                intent.setClass(getActivity(), BucketAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        mBtnBookSetting.setOnClickListener(this);

        popupView = getActivity().getLayoutInflater().inflate(R.layout.book_setting_menu, null);
        PopupWindowViewHolder holder = new PopupWindowViewHolder(popupView);
        popupView.setTag(holder);
        holder.mBtnBucketStatusAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.hide();
            }
        });
        holder.mBtnBucketStatusSeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.hide();
            }
        });
        holder.mBtnBucketStatusDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.hide();
            }
        });
        mPopupWindow = AndroidUtils.makePopupWindow(popupView);
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)


        mBookCover.setOnClickListener(this);
        mBookTitle.setOnClickListener(this);
        mBookDudate.setOnClickListener(this);
        mBookStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId() == mBtnBookSetting.getId()){
            if(mPopupWindow != null && !mPopupWindow.isShowing() && !view.isSelected()){
                mPopupWindow.showAsDropDown(view);
                view.setSelected(true);
            }else{
                mPopupWindow.hide();
                view.setSelected(false);
            }
        }
        if(view == mBookCover || view == mBookDudate || view == mBookDudate || view == mBookStatus){
            Intent intent = new Intent();
            intent.setClass(getActivity(), BucketAddActivity.class);
            intent.putExtra("bucketId", bucket.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        if(mBookCoverImage.getDrawable() != null){
            BitmapDrawable drawable = (BitmapDrawable) mBookCoverImage.getDrawable();
            mBookCoverImage.setImageDrawable(null);
            Bitmap bitmap = drawable.getBitmap();
            bitmap.recycle();

        }
        super.onDestroyView();

    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'null'
     * for easy to all layout elements.
     *
     * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
     */
    class PopupWindowViewHolder {
        @InjectView(R.id.btn_bucket_status_achievement)
        Button mBtnBucketStatusAchievement;
        @InjectView(R.id.btn_bucket_status_seal)
        Button mBtnBucketStatusSeal;
        @InjectView(R.id.btn_bucket_status_delete)
        Button mBtnBucketStatusDelete;

        PopupWindowViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
    public class ImageDownloadTask extends CustomAsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                File downloadedFileName = FileUtils.getDownloadFromUrl(params[0]);
                Bitmap bitmap = ImageUtil.getBitmap(downloadedFileName, (int) getResources().getDimension(R.dimen.book_width_dp), (int) getResources().getDimension(R.dimen.book_height_dp));
                return bitmap;
            } catch (Exception e) {
                Log.e("dream", e.getMessage());
            }

            return null;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                //Drawable drawable = new BitmapDrawable( getResources(), bitmap );
                if(mBookCoverImage.getDrawable() == null) {
                    mBookCoverImage.setImageBitmap(bitmap);
                }
            }
        }
    }
}
