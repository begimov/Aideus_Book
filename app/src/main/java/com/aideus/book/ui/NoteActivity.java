package com.aideus.book.ui;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.aideus.book.R;
import com.aideus.book.ui.fragments.NoteFragment;

public class NoteActivity extends Activity implements NoteFragment.Contract {

    static final String EXTRA_POSITION = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            int position = getIntent().getIntExtra(EXTRA_POSITION, -1);
            if (position >= 0) {
                Fragment frag = NoteFragment.newInstance(position);
                getFragmentManager().beginTransaction().add(android.R.id.content, frag).commit();
            }
        }

    }

    @Override
    public void closeNotes() {
        finish();
    }
}
