package com.example.android.newbookapp;

/**
 * Book
 * A custom class to store Book information
 */
public class Book {

    private static final String USGS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=1";

    private String mTitle;
    private String mAuthors;
    private String mUrl;


    public Book(String title, String authors, String url){
        mTitle = title;
       mAuthors = authors;
        mUrl = url;
    }


        public String getTitle() {
            return mTitle;
        }

        public String getAuthors() {
            return mAuthors;
        }

    public String getUrl() {
        return mUrl;
    }
    }


