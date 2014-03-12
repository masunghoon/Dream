// Generated code from Butter Knife. Do not modify!
package com.vivavu.dream.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainContentsFragment$$ViewInjector {
  public static void inject(Finder finder, final com.vivavu.dream.fragment.MainContentsFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131230851);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230851' for field 'mBucketList' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBucketList = (android.widget.ListView) view;
  }

  public static void reset(com.vivavu.dream.fragment.MainContentsFragment target) {
    target.mBucketList = null;
  }
}
