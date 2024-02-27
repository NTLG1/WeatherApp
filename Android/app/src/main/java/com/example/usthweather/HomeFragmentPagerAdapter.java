package com.example.usthweather;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.content.Context;
import android.util.Log;

public class HomeFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private final int PAGE_COUNT = 3;

    private String[] titles;
    private Double[] lat;
    private Double[] lon;
    private Boolean isCelsius;

    private String dateFormat;
    private String APIKey;

    public HomeFragmentPagerAdapter(FragmentManager fm, String[] titles, Double[] lat, Double[] lon, Boolean isCelsius, String dateFormat, String APIKey) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.titles = titles;
        this.lat = lat;
        this.lon = lon;
        this.isCelsius = isCelsius;
        this.dateFormat = dateFormat;
        this.APIKey = APIKey;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int page) {
        Log.d("Celsius", "true");
        switch (page) {
            case 0:
                return new WeatherAndForecastFragment(lat[0], lon[0], isCelsius, dateFormat, APIKey);
            case 1:
                return new WeatherAndForecastFragment(lat[1], lon[1], isCelsius, dateFormat, APIKey);
            case 2:
                return new WeatherAndForecastFragment(lat[2], lon[2], isCelsius, dateFormat, APIKey);
        }
        return new Fragment();
    }

    @Override
    public CharSequence getPageTitle(int page) {
        return titles[page];
    }
}
