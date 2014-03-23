package com.vivavu.dream.fragment;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.vivavu.dream.R;

import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-21.
 */
public abstract class CustomPullToRefreshFragment<T extends android.view.View> extends CustomBaseFragment implements PullToRefreshListView.OnRefreshListener<T> {
    static public final int SEND_REFRESH_START = 0;
    static public final int SEND_REFRESH_STOP = 1;
    static public final int SEND_BUKET_LIST_UPDATE = 2;
    @InjectView(R.id.list)
    protected PullToRefreshListView mList;

    protected abstract void updateContents();

    @Override
    public abstract void onRefresh(PullToRefreshBase<T> listViewPullToRefreshBase);
}
