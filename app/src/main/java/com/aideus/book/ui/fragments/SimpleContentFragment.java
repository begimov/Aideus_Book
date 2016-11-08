package com.aideus.book.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewFragment;

public class SimpleContentFragment extends WebViewFragment {

    private static final String PREF_FONT_SIZE = "fontSize";

    private static final String KEY_FILE="file";

    private static final String WEB_VIEW_DEFAULT_ENCODING = "utf-8";

    private static int mFontSize = 16;

    public static SimpleContentFragment newInstance(final String file, Context context) {
        SimpleContentFragment f = new SimpleContentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FILE, file);
        f.setArguments(args);

        //TODO Use separated PreferenceHelper class, change with TODO in ModelFragment
        //TODO optimize not accessing Preferences every time instance been created
        //TODO Data access not using ModelFragment!
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs != null) {
            mFontSize = prefs.getInt(PREF_FONT_SIZE, 16);
        }

        return(f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result=
                super.onCreateView(inflater, container, savedInstanceState);

        WebSettings webSettings = getWebView().getSettings();
        webSettings.setDefaultTextEncodingName(WEB_VIEW_DEFAULT_ENCODING);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);

        //TODO Dynamic text size with ui controls
        webSettings.setDefaultFontSize(mFontSize);
        //TODO Update font size in webview and/or page

        getWebView().loadUrl(getPage());

        return(result);
    }

    private String getPage() {
        return(getArguments().getString(KEY_FILE));
    }
}
