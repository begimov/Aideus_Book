package com.aideus.book.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.aideus.book.ui.fragments.SimpleContentFragment;

public class SimpleContentActivity extends Activity {

    public static final String EXTRA_FILE = "file";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            String file = getIntent().getStringExtra(EXTRA_FILE);
            Fragment f = SimpleContentFragment.newInstance(file, this);
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, f).commit();
        }
    }
}
