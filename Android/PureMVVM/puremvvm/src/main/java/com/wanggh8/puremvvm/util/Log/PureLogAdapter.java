package com.wanggh8.puremvvm.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;

/**
 * LogAdapter
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/12/25
 */
public class PureLogAdapter extends AndroidLogAdapter {

    public PureLogAdapter() {
        super();
    }

    public PureLogAdapter(FormatStrategy formatStrategy) {
        super(formatStrategy);
    }

    @Override
    public boolean isLoggable(int priority, String tag) {
        if (tag!= null && tag.contains("NetHttp-")) {
            return false;
        }
        return true;
    }
}
