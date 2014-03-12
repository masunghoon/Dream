// Generated code from Butter Knife. Do not modify!
package com.vivavu.dream.adapter.bucket;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class PlanAdapter$PlanPopupDoneViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.vivavu.dream.adapter.bucket.PlanAdapter.PlanPopupDoneViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131230871);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230871' for field 'mRadioBtnDoneTrue' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mRadioBtnDoneTrue = (android.widget.RadioButton) view;
    view = finder.findById(source, 2131230872);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230872' for field 'mRadioBtnDoneFalse' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mRadioBtnDoneFalse = (android.widget.RadioButton) view;
    view = finder.findById(source, 2131230870);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230870' for field 'mRadioGroupPopupPlanDone' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mRadioGroupPopupPlanDone = (android.widget.RadioGroup) view;
    view = finder.findById(source, 2131230873);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230873' for field 'mBtnPopupPlanCamera' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBtnPopupPlanCamera = (android.widget.Button) view;
    view = finder.findById(source, 2131230874);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230874' for field 'mBtnPopupPlanShareFacebook' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBtnPopupPlanShareFacebook = (android.widget.Button) view;
    view = finder.findById(source, 2131230875);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230875' for field 'mBtnPopupPlanShareTwitter' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBtnPopupPlanShareTwitter = (android.widget.Button) view;
    view = finder.findById(source, 2131230876);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230876' for field 'mBtnPopupPlanShareGooglePlus' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBtnPopupPlanShareGooglePlus = (android.widget.Button) view;
  }

  public static void reset(com.vivavu.dream.adapter.bucket.PlanAdapter.PlanPopupDoneViewHolder target) {
    target.mRadioBtnDoneTrue = null;
    target.mRadioBtnDoneFalse = null;
    target.mRadioGroupPopupPlanDone = null;
    target.mBtnPopupPlanCamera = null;
    target.mBtnPopupPlanShareFacebook = null;
    target.mBtnPopupPlanShareTwitter = null;
    target.mBtnPopupPlanShareGooglePlus = null;
  }
}
