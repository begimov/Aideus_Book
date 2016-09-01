package com.aideus.book.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;

import com.aideus.book.R;
import com.aideus.book.data.local.DatabaseHelper;
import com.aideus.book.events.NoteLoadedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NoteFragment extends Fragment implements TextWatcher {

    public interface Contract {
        void closeNotes();
    }

    private static final String KEY_POSITION = "position";

    private EditText mEditor = null;

    private Intent mShareIntent = new Intent(Intent.ACTION_SEND).setType("text/plain");

    public static NoteFragment newInstance(int position) {
        NoteFragment frag = new NoteFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);
        return (frag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.editor, container, false);
        mEditor = (EditText) result.findViewById(R.id.editor);
        mEditor.addTextChangedListener(this);

        Button buttonSave = (Button) result.findViewById(R.id.btn_editor_save);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContract().closeNotes();
            }
        });

        return (result);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (TextUtils.isEmpty(mEditor.getText())) {
            DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
            db.loadNote(getPosition());
        }
    }

    @Override
    public void onPause() {
        DatabaseHelper.getInstance(getActivity())
                .updateNote(getPosition(),
                        mEditor.getText().toString());
        //TODO check if mEditor state is not changed from last database state
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notes, menu);
        ShareActionProvider mShareActionProvider = null;
        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();
        mShareActionProvider.setShareIntent(mShareIntent);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            mEditor.setText(null);
            getContract().closeNotes();
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    private int getPosition() {
        return (getArguments().getInt(KEY_POSITION, -1));
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NoteLoadedEvent event) {
        if (event.getPosition() == getPosition()) {
            mEditor.setText(event.getProse());
        }
    }

    private Contract getContract() {
        return ((Contract) getActivity());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mShareIntent.putExtra(Intent.EXTRA_TEXT, s.toString());
    }
}
