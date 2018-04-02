package com.example.android.abnd_project_five_and_six;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    /**
     * URL for news data from the The Guardian.
     */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?q";

    /**
     * Additional queries for The Guardian API
     * and their values (except ones which defined in Settings)
     */

    private static final String API_KEY = "api-key";
    private static final String API_KEY_VALUE = "180f2602-4726-43d6-bf1e-55256d6ccbc8";
    private static final String SHOW_FIELDS = "show-fields";
    private static final String SHOW_FIELDS_VALUE = "byline";
    private static final String ORDER_BY = "order-by";
    private static final String PAGE_SIZE = "page-size";

    /**
     * Constant value for the news loader ID.
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * Adapter for the list of news.
     */
    private NewsAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        // Find a reference to the ListView in the layout
        ListView newsListView = findViewById(R.id.list_news);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the ListView
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news.
                News currentNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object.
                if (currentNews != null) {
                    Uri newsUri = Uri.parse(currentNews.getUrl());

                    // Create a new intent to view the news URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                    // Check whatever is an activity available that can respond to the intent
                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(websiteIntent,
                            PackageManager.MATCH_DEFAULT_ONLY);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        // Send the intent to launch a new activity
                        startActivity(websiteIntent);
                    } else {
                        // If there is no web-browser
                        // Show Toast message with warning
                        Toast.makeText(getApplicationContext(), getString(R.string.no_browser), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        // Prevent null pointer exception in getActiveNetworkInfo()
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // If there is a network connection, fetch data
            if (networkInfo != null && networkInfo.isConnected()) {
                // Get a reference to the LoaderManager, in order to interact with loaders.
                LoaderManager loaderManager = getLoaderManager();

                // Initialize the loader.
                loaderManager.initLoader(NEWS_LOADER_ID, null, this);

            } else {
                // Otherwise, display error
                // Hide loading indicator so error message will be visible
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.GONE);

                // Update empty state with no connection error message
                mEmptyStateTextView.setText(R.string.no_internet_connection);
            }
        } else {
            // Otherwise, display error with no connection error message
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Initialize floating action button (FAB)
        // Setup listener
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            // Refresh a news by pushing the FAB
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences
        // The second parameter is the default value
        // This one for order of displaying News
        String orderByValue = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));


        // This one for number of stories
        String pageSizeValue = sharedPreferences.getString(
                getString(R.string.settings_news_number_key),
                getString(R.string.settings_min_news_number_default));

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Additional query parameters.
        // Allow users to customize them via Settings
        uriBuilder.appendQueryParameter(API_KEY, API_KEY_VALUE);
        uriBuilder.appendQueryParameter(SHOW_FIELDS, SHOW_FIELDS_VALUE);
        uriBuilder.appendQueryParameter(ORDER_BY, orderByValue);
        uriBuilder.appendQueryParameter(PAGE_SIZE, pageSizeValue);
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous data
        mAdapter.clear();

        // If there is a valid list of News, then add them to the adapter's data set
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
