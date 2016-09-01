package com.aideus.book.events;

public class NoteLoadedEvent {

    private int mPosition;

    private String mProse;

    public NoteLoadedEvent(final int position, final String prose) {
        mPosition =position;
        mProse = prose;
    }

    public int getPosition() {
        return(mPosition);
    }

    public String getProse() {
        return(mProse);
    }
}
