package com.aideus.book.data.remote;

import com.aideus.book.data.remote.model.BookUpdateInfo;

import retrofit.http.GET;

interface IBookUpdate {
    @GET("/book_apps/aideusbook-update.json")
    BookUpdateInfo update();
}
