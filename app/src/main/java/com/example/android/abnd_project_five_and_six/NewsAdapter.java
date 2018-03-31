package com.example.android.abnd_project_five_and_six;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * An {@link NewsAdapter} knows how to create a list item layout for each news
 * in the data source (a list of {@link News} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String DATE_SEPARATOR = "T";
    private static final String TIME_SEPARATOR = "Z";

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param news    is the list of news, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    /**
     * Returns a list item view that displays information about the news at the given position in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        // Find the news piece at the given position in the list of news
        News currentNews = getItem(position);

        // Find the TextView with view ID section
        TextView sectionView = listItemView.findViewById(R.id.news_section);
        // Display the section of the current news in that TextView
        sectionView.setText(currentNews.getNewsSection());

        TextView titleView = listItemView.findViewById(R.id.news_title);
        titleView.setText(currentNews.getNewsTitle());

        TextView authorView = listItemView.findViewById(R.id.news_author);
        authorView.setText(currentNews.getNewsAuthor());

        // Handle the date
        String originalDate = currentNews.getNewsDate();
        String dateOffset = "";
        String dateLeftover;
        String timeOffset = "";

        if (originalDate.contains(DATE_SEPARATOR)) {
            // Split the string into different parts (as an array of Strings)
            // based on the "T" text. We expect an array of 2 Strings, where
            // the first String will be "date" and the second String will be "time".
            String[] date = originalDate.split(DATE_SEPARATOR);
            dateOffset = date[0];
            dateLeftover = date[1];
            // Do the same with "leftover" of date, which contains time and separator "Z"
            String[] time = dateLeftover.split(TIME_SEPARATOR);
            timeOffset = time[0];
        }

        // Find the TextView with view ID location
        TextView dateView = listItemView.findViewById(R.id.news_date);
        // Display the date and time and "," between them
        dateView.setText(dateOffset + ", " + timeOffset);

//        TextView dateView = listItemView.findViewById(R.id.news_date);
//        dateView.setText(currentNews.getNewsDate());

        return listItemView;
    }
}

