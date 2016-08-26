package com.aideus.book.data.local;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.aideus.book.data.local.model.BookContents;
import com.aideus.book.ui.fragments.SimpleContentFragment;

public class ContentsAdapter extends FragmentStatePagerAdapter {

    private final BookContents mContents;

    public ContentsAdapter(final Activity activity, final BookContents contents) {
        super(activity.getFragmentManager());
        mContents = contents;
    }

    @Override
    public Fragment getItem(final int position) {
        return (SimpleContentFragment.newInstance(mContents.getChapterPath(position)));
    }

    @Override
    public int getCount() {
        return (mContents.getChapterCount());
    }
}
