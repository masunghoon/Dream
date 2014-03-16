package com.vivavu.dream.fragment.bucket.option;

import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.bucket.option.Option;

/**
 * Created by yuja on 14. 3. 14.
 */
public abstract class OptionBaseFragment<T extends Option> extends CustomBaseFragment {
    protected T contents;
    protected boolean displayMode;
    public OptionBaseFragment(T originalData) {
        this.contents = originalData;
    }

    public T getContents() {
        bind();
        return contents;
    }

    public void setContents(T contents) {
        this.contents = contents;
        update();
    }

    public abstract void update();
    public abstract void bind();

    public boolean isDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(boolean displayMode) {
        this.displayMode = displayMode;
    }
}
