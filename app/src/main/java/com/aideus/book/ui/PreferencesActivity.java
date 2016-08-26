package com.aideus.book.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.aideus.book.R;

public class PreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getFragmentManager().findFragmentById(android.R.id.content)==null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new Display())
                    .commit();
        }
    }

    public static class Display extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_display);
        }
    }
}
