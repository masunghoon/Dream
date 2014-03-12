// Generated code from Butter Knife. Do not modify!
package com.vivavu.dream.adapter.bucket;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class PlanAdapter$PlanAdapterViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.vivavu.dream.adapter.bucket.PlanAdapter.PlanAdapterViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131230869);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230869' for field 'mPlanItemDate' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mPlanItemDate = (android.widget.TextView) view;
    view = finder.findById(source, 2131230796);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230796' for field 'mBucketBtnDone' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBucketBtnDone = (android.widget.Button) view;
    view = finder.findById(source, 2131230797);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230797' for field 'mBucketItemTitle' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBucketItemTitle = (android.widget.TextView) view;
    view = finder.findById(source, 2131230799);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230799' for field 'mBucketDefaultCardBtnDday' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBucketDefaultCardBtnDday = (android.widget.ImageView) view;
    view = finder.findById(source, 2131230800);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230800' for field 'mBucketItemScope' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBucketItemScope = (android.widget.TextView) view;
    view = finder.findById(source, 2131230801);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230801' for field 'mBucketItemRemain' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBucketItemRemain = (android.widget.TextView) view;
    view = finder.findById(source, 2131230802);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230802' for field 'mBucketItemProgressbar' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBucketItemProgressbar = (android.widget.ProgressBar) view;
    view = finder.findById(source, 2131230824);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230824' for field 'mBucketItemBtnLike' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBucketItemBtnLike = (android.widget.Button) view;
    view = finder.findById(source, 2131230825);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230825' for field 'mBucketItemBtnReply' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBucketItemBtnReply = (android.widget.Button) view;
  }

  public static void reset(com.vivavu.dream.adapter.bucket.PlanAdapter.PlanAdapterViewHolder target) {
    target.mPlanItemDate = null;
    target.mBucketBtnDone = null;
    target.mBucketItemTitle = null;
    target.mBucketDefaultCardBtnDday = null;
    target.mBucketItemScope = null;
    target.mBucketItemRemain = null;
    target.mBucketItemProgressbar = null;
    target.mBucketItemBtnLike = null;
    target.mBucketItemBtnReply = null;
  }
}
