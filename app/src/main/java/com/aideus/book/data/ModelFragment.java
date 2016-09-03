package com.aideus.book.data;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aideus.book.data.local.model.BookContents;
import com.aideus.book.data.remote.DownloadCheckService;
import com.aideus.book.events.BookLoadedEvent;
import com.aideus.book.events.BookUpdatedEvent;
import com.aideus.book.ui.fragments.SimpleContentFragment;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ModelFragment extends Fragment {

    private static final String PREF_LAST_POSITION = "lastPosition";

    private static final String PREF_SAVE_LAST_POSITION = "saveLastPosition";

    private BookContents mBook = null;

    private SharedPreferences mPrefs = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity host) {
        super.onAttach(host);
        EventBus.getDefault().register(this);
        if (mBook == null) {
            new LoadThread(host).start();
        }
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    synchronized public BookContents getBook() {
        return(mBook);
    }

    public void setPrefLastPosition(int position) {
        if (mPrefs != null) {
            mPrefs.edit().putInt(PREF_LAST_POSITION, position)
                    .apply();
        }
    }

    public int getPrefLastPosition() {
        if (mPrefs != null) {
            if (mPrefs.getBoolean(PREF_SAVE_LAST_POSITION, true)) {
                return mPrefs.getInt(PREF_LAST_POSITION, 0);
            }
        }
        return 0;
    }

    public int getChaptersCount() {
        return mBook.getChaptersCount();
    }

    public String getChapterTitle(int chapter) {
        return mBook.getChapterTitle(chapter);
    }

    public Fragment getSimpleContentFragment(int position, Activity activity) {
        return SimpleContentFragment.newInstance(mBook.getChapterPath(position), activity);
    }

    public void startDownloadCheckService(Context context) {
        context.startService(new Intent(context, DownloadCheckService.class));
    }

    private class LoadThread extends Thread {

        static final String CONTENTS_JSON_FILENAME = "contents.json";

        static final String BOOK_CONTENTS_JSON_LOCAL_URI = "book/contents.json";

        private Context context = null;

        LoadThread (final Context context) {
            super();
            this.context = context.getApplicationContext();
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            synchronized (this) {
                //TODO Move to separated PreferenceHelper class
                mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            }
            Gson gson = new Gson();
            File baseDir = new File(context.getFilesDir(), DownloadCheckService.UPDATE_BASEDIR);
            try {
                InputStream is;
                if (baseDir.exists()) {
                    is = new FileInputStream(new File(baseDir, CONTENTS_JSON_FILENAME));
                }
                else {
                    is = context.getAssets().open(BOOK_CONTENTS_JSON_LOCAL_URI);
                }
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(is));
                synchronized(this) {
                    mBook = gson.fromJson(reader, BookContents.class);
                }
                is.close();
                if (baseDir.exists()) {
                    mBook.setBaseDir(baseDir);
                }
                EventBus.getDefault().post(new BookLoadedEvent());
            }
            catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(BookUpdatedEvent event) {
        if (getActivity() != null) {
            new LoadThread(getActivity()).start();
        }
    }

}
