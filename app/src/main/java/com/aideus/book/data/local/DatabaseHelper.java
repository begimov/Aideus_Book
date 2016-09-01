package com.aideus.book.data.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;

import com.aideus.book.events.NoteLoadedEvent;

import org.greenrobot.eventbus.EventBus;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aideusbook.db";

    private static final int SCHEMA_VERSION = 1;

    private static DatabaseHelper sSingleton = null;

    public synchronized static DatabaseHelper getInstance(final Context context) {
        if (sSingleton == null) {
            sSingleton = new DatabaseHelper(context.getApplicationContext());
        }
        return (sSingleton);
    }

    private DatabaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (position INTEGER PRIMARY KEY, prose TEXT)");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db,
                          final int oldVersion,
                          final int newVersion) {
        throw new RuntimeException("This should not be called");
    }

    public void loadNote(final int position) {
        new LoadThread(position).start();
    }

    public void updateNote(final int position, final String prose) {
        new UpdateThread(position, prose).start();
    }

    private class LoadThread extends Thread {

        private int position = -1;

        LoadThread(final int position) {
            super();
            this.position = position;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            String[] args = {String.valueOf(position)};
            Cursor c = getReadableDatabase()
                    .rawQuery("SELECT prose FROM notes WHERE position = ? ", args);
            if (c.getCount() > 0) {
                c.moveToFirst();
                EventBus.getDefault().post(new NoteLoadedEvent(position, c.getString(0)));
            }
            c.close();
        }
    }

    private class UpdateThread extends Thread {

        private int position = -1;

        private String prose = null;

        UpdateThread(final int position, final String prose) {
            super();
            this.position = position;
            this.prose = prose;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            String[] args = {String.valueOf(position), prose};
            getWritableDatabase().execSQL("INSERT OR REPLACE INTO notes (position, prose) VALUES (?, ?)", args);
        }
    }
}
