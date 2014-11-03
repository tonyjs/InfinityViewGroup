package com.tonyjs.infinityviewgroup.lib;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by im026 on 14. 11. 3..
 */
public abstract class InfinityPagerAdapter extends PagerAdapter {

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return false;
    }

    public abstract Object instantiateItem(ViewGroup container, int position, boolean addToFirst);

}
