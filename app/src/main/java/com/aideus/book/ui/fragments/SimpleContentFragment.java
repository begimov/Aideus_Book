package com.aideus.book.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebViewFragment;

public class SimpleContentFragment extends WebViewFragment {

    private static final String KEY_FILE="file";

    private static final String WEB_VIEW_DEFAULT_ENCODING = "utf-8";

    public static SimpleContentFragment newInstance(final String file) {
        SimpleContentFragment f = new SimpleContentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FILE, file);
        f.setArguments(args);
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

        WebSettings settings = getWebView().getSettings();
        settings.setDefaultTextEncodingName(WEB_VIEW_DEFAULT_ENCODING);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        //TODO Dynamic text size with ui controls
        settings.setTextZoom(settings.getTextZoom() + 10);

        getWebView().loadUrl(getPage());
        return(result);
    }

    private String getPage() {
        return(getArguments().getString(KEY_FILE));
    }
}
