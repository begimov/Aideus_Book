package com.aideus.book.data.remote;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.aideus.book.data.remote.model.BookUpdateInfo;
import com.aideus.book.events.BookUpdatedEvent;
import com.commonsware.cwac.security.ZipUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import retrofit.RestAdapter;

public class DownloadCheckService extends IntentService {

    public static final String UPDATE_BASEDIR = "updates";

    private static final String UPDATE_BOOK_DATE = "20160811";

    private static final String BASE_UPDATE_URL = "http://aideus.ru";

    private static final String UPDATE_FILENAME = "aideusbook.zip";

    public DownloadCheckService() {
        super("DownloadCheckService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String url = getUpdateUrl();
            if (url != null) {
                File book = download(url);
                //TODO Clean UPDATE_BASEDIR before unpacking ZIP
                File updateDir = new File(getFilesDir(), UPDATE_BASEDIR);
                updateDir.mkdirs();
                ZipUtils.unzip(book, updateDir);
                book.delete();
                EventBus.getDefault().post(new BookUpdatedEvent());
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(),
                    "Exception downloading update", e);
        }
    }

    private String getUpdateUrl() {
        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint(BASE_UPDATE_URL)
                .build();
        IBookUpdate updateInterface = retrofit.create(IBookUpdate.class);
        BookUpdateInfo info = updateInterface.update();
        if (info.updatedOn.compareTo(UPDATE_BOOK_DATE) > 0) {
            //TODO Compare updateOn to last saved value of it, not to a constant, save last updatedOn in SharedPreferences
            return (info.updateUrl);
        }
        return null;
    }

    private File download(String url) throws IOException {
        File output = new File(getFilesDir(), UPDATE_FILENAME);
        if (output.exists()) {
            output.delete();
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        BufferedSink sink = Okio.buffer(Okio.sink(output));
        sink.writeAll(response.body().source());
        sink.close();
        return (output);
    }
}


