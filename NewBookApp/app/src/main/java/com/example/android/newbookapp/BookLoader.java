package com.example.android.newbookapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Alex Manda on 29/07/2017.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>>{
    private static final String LOG_TAG = BookLoader.class.getName();

    private String mUrl;

    public BookLoader (Context context,String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}
