package com.vivavu.dream.fragment.bucket;

import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.bucket.option.Option;

/**
 * Created by yuja on 14. 3. 14.
 */
public abstract class OptionBaseFragment<T extends Option> extends CustomBaseFragment {
    T userInput;
    T originalData;

    public OptionBaseFragment(T originalData) {
        this.originalData = originalData;
        this.userInput = (T) originalData.clone();
    }

    public abstract T getContents();
}
