package com.aideus.book.ui;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aideus.book.R;

public class FontSizeDialogPreference extends DialogPreference {

    private static final int DEFAULT_FONT_SIZE = 16;

    private int mFontSize = DEFAULT_FONT_SIZE;

    public FontSizeDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_slider);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        final TextView textView = (TextView) view.findViewById(R.id.tv_TextSizeExample);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar1);
        seekBar.setProgress(mFontSize - DEFAULT_FONT_SIZE);
        textView.setTextSize(mFontSize);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFontSize = progress + DEFAULT_FONT_SIZE;
                textView.setTextSize(mFontSize);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mFontSize = this.getPersistedInt(DEFAULT_FONT_SIZE);
        }
        super.onSetInitialValue(restorePersistedValue, defaultValue);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(mFontSize);
        }
        super.onDialogClosed(positiveResult);
    }
}
