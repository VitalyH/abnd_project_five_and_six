package com.example.android.abnd_project_five_and_six;

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
 * Helper methods related to requesting and receiving news data from The Guardian.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     *  Key values for JSON request,
     */
    private static final String RESPONSE = "response";
    private static final String RESULTS= "results";
    private static final String FIELDS= "fields";

    /**
     * Create a private constructor - class where we can hold static variables and methods.
     * They may be accessed directly from the class name QueryUtils.
     */
    private QueryUtils() {
    }

    /**
     * Query the The Guardian news and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and
        // return the list of News
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
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
            if (urlConnection.getResponseCode() == 200) {
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

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem, a JSONException
        // exception object will be thrown.
        // Catch the exception so the app doesn't crash and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject guardianJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONArray "results" which is within the JSONObject "response"
            JSONArray newsArray = guardianJsonResponse.getJSONObject(RESPONSE).getJSONArray(RESULTS);

            // For each News in the NewsArray, create a News object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list of "news pieces"
                JSONObject currentNews = newsArray.getJSONObject(i);

                // Extract the section name
                String section = currentNews.getString("sectionName");

                // Extract the title of article
                String title = currentNews.getString("webTitle");

                // Extract the name of the author
                String author = "";
                // News may not have additional fields - checking
                if (currentNews.has(FIELDS)) {
                    JSONObject myObject = currentNews.getJSONObject("fields");
                    // Extract the author of the current news
                    author = myObject.getString("byline");
                }
                // Extract the date of news
                String date = currentNews.getString("webPublicationDate");

                // Extract the URL mews
                String url = currentNews.getString("webUrl");

                // Create a new News object.
                News news = new News(section, title, author, date, url);
                // News news = new News(section, title, date, url);

                // Add the new News object to the list of news.
                newsList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception, so the app doesn't crash. Print a log message.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of news
        return newsList;
    }
}
