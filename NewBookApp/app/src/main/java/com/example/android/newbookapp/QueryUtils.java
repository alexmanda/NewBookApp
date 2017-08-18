package com.example.android.newbookapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex Manda on 29/07/2017.
 */

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<Book> fetchBookData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Book> books = extractFeatureFromJson(jsonResponse);
        return books;
    }


    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static List<Book> extractFeatureFromJson(String bookJSON) {
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        List<Book> books = new ArrayList<>();

        try {
            // Convert the results from String to JSONObject
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            // Get the items node (Books) from the JSONObject
            JSONArray resultsArray = baseJsonResponse.getJSONArray("items");
            // Count the results
            int countResults = resultsArray.length();
            // Create an ArrayList to hold the parsed collection of results
            ArrayList<Book> parsedResults = new ArrayList<>();
            // Loop through the resultsArray to parse the Books out
            for (int i = 0; i < countResults; i++) {
                // Get the Book record
                JSONObject bookRecord = resultsArray.getJSONObject(i);
                // Get the volume info node from the Book record
                JSONObject bookVolumeInfo = bookRecord.getJSONObject("volumeInfo");
                // Get the title from the volume info
                String bookTitle = bookVolumeInfo.getString("title");
                //------------------------------------------------------------------------------
                // AUTHORS
                //------------------------------------------------------------------------------
                // Some books don't have an authors node, use try/catch to prevent null pointers
                JSONArray bookAuthors = null;
                try {
                    bookAuthors = bookVolumeInfo.getJSONArray("authors");
                } catch (JSONException ignored) {
                }
                // Convert the authors to a string
                String bookAuthorsString = "";
                // If the author is empty, set it as "Unknown"
                if (bookAuthors == null) {
                    bookAuthorsString = "Unknown";
                } else {
                    // Format the authors as "author1, author2, and author3"
                    int countAuthors = bookAuthors.length();
                    for (int e = 0; e < countAuthors; e++) {
                        String author = bookAuthors.getString(e);
                        if (bookAuthorsString.isEmpty()) {
                            bookAuthorsString = author;
                        } else if (e == countAuthors - 1) {
                            bookAuthorsString = bookAuthorsString + " and " + author;
                        } else {
                            bookAuthorsString = bookAuthorsString + ", " + author;
                        }
                    }
                }
                //------------------------------------------------------------------------------
                // IMAGE LINKS
                //------------------------------------------------------------------------------
                JSONObject bookImageLinks = null;
                try {
                    bookImageLinks = bookVolumeInfo.getJSONObject("imageLinks");
                } catch (JSONException ignored) {
                }
                // Convert the image link to a string
                String bookSmallThumbnail = "";
                if (bookImageLinks == null) {
                    bookSmallThumbnail = "null";
                } else {
                    bookSmallThumbnail = bookImageLinks.getString("smallThumbnail");
                }
                // Create a Book object
                Book mBook = new Book(bookTitle, bookAuthorsString, bookSmallThumbnail);
                // Add it to the array
                parsedResults.add(i, mBook);
            }
            // Return the results
            return parsedResults;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

