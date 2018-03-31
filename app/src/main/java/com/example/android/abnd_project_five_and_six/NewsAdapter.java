package com.example.android.abnd_project_five_and_six;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * An {@link NewsAdapter} knows how to create a list item layout for each news
 * in the data source (a list of {@link News} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param news is the list of news, which is the data source of the adapter
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

        TextView dateView = listItemView.findViewById(R.id.news_date);
        dateView.setText(currentNews.getNewsDate());

        TextView authorView = listItemView.findViewById(R.id.news_author);
        authorView.setText(currentNews.getNewsAuthor());

        return listItemView;
    }
}

