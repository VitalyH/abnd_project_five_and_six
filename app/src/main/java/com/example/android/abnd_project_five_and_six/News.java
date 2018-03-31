package com.example.android.abnd_project_five_and_six;

/**
 * An {@link News} object contains information about single "piece" of news.
 */
public class News {

    /**
     * News title
     */
    private String mTitle;

    /**
     * News section
     */
    private String mSection;

    /**
     * News author
     */
    private String mAuthor;

    /**
     * News date
     */
    private String mDate;

    /**
     * Website URL of the news
     */
    private String mUrl;

    /**
     * Constructs a new {@link News} object.
     *
     * @param section is the title of the news section
     * @param title   is the news title
     * @param author  is the author of the news
     * @param date    is the news date
     * @param url     is the website URL to the news
     */
    public News(String section, String title, String date, String url) {
        mSection = section;
        mTitle = title;
      //  mAuthor = author;
        mDate = date;
        mUrl = url;
    }

    /**
     * Returns the news section.
     */
    public String getNewsSection() {
        return mSection;
    }

    /**
     * Returns the news title.
     */
    public String getNewsTitle() {
        return mTitle;
    }

    /**
     * Returns the news author.
     */
    public String getNewsAuthor() {
        return mAuthor;
    }

    /**
     * Returns the news date.
     */
    public String getNewsDate() {
        return mDate;
    }

    /**
     * Returns the website URL.
     */
    public String getUrl() {
        return mUrl;
    }
}



