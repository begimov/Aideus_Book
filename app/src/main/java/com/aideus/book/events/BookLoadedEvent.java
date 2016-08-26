package com.aideus.book.events;

import com.aideus.book.data.local.model.BookContents;

public class BookLoadedEvent {

    private BookContents mContents = null;

    public BookLoadedEvent(BookContents contents) {
        mContents = contents;
    }

    public BookContents getBook() {
        return(mContents);
    }
}
