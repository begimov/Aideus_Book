package com.aideus.book.data.local;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.aideus.book.data.ModelFragment;

public class ContentsAdapter extends FragmentStatePagerAdapter {

    private final ModelFragment mFrag;

    private final Activity mActivity;

    public ContentsAdapter(final ModelFragment modelFragment,
                           final Activity activity) {
        super(activity.getFragmentManager());
        mFrag = modelFragment;
        mActivity = activity;
    }

    @Override
    public Fragment getItem(final int position) {
        return (mFrag.getSimpleContentFragment(position, mActivity));
    }

    @Override
    public int getCount() {
        return (mFrag.getChaptersCount());
    }
}
