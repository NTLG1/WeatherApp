package com.example.usthweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;


import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class WeatherActivity extends AppCompatActivity implements ApiKeyInputDialogFragment.ApiKeyListener {
        private String[] titles = new String[3];
        private Double[] lat = new Double[3];
        private Double[] lon = new Double[3];
        private static final int SETTINGS_REQUEST_CODE = 123;

        private boolean shouldUpdateUI = false;
//    AudioPlayer audioPlayer = new AudioPlayer(this);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The temperature unit or date format has been changed, set the flag to update UI on resume
            shouldUpdateUI = true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldUpdateUI) {
            updateUI();
            shouldUpdateUI = false;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If app launch for the first time trigger API Input
        if (isAPIKeyNotSet())  showAPIKeyInputDialog();
        // Check and retrieve the saved temperature unit preference
        boolean isCelsius = getSavedTemperatureUnitPreference();
        // Check and retrieve the saved date format preference
        String dateFormat = getSavedDateFormatPreference();
        // Check and retrieve the saved date format preference
        String APIkey = getSavedAPIKeyPreference();
        // Retrieve the saved language preference
        String selectedLanguage = getSavedLanguagePreference();
        // Check if the language has been changed from settings
        if (getIntent().hasExtra("LangChanged")) {
            String changedLanguage = getIntent().getStringExtra("LangChanged");
            setLocale(changedLanguage);
        } else if (!TextUtils.isEmpty(selectedLanguage)) {
            // Set the locale based on the saved language preference
            setLocale(selectedLanguage);
        }

        String title = getResources().getString(R.string.app_name);
        setTitle(title);
        setContentView(R.layout.activity_weather);

        for (int i = 0; i < titles.length; i++) {
            titles[i] = getCity(i + 1);
            lat[i] = getLat(i + 1);
            lon[i] = getLon(i + 1);
        }

        PagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), titles, lat, lon, isCelsius, dateFormat, APIkey);
        ViewPager pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(pager);

//        if (savedInstanceState == null) {
//            Toast.makeText(this, getString(R.string.playingmusic), Toast.LENGTH_SHORT).show();
//            audioPlayer.playAudio();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            updateUI();
            Toast.makeText(this, getString(R.string.refresh_toast), Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.settings) {
            Intent settingsIntent = new Intent(this, PrefActivity.class);
            settingsIntent.putExtra("Lang", getResources().getString(R.string.settings));
            startActivityForResult(settingsIntent, SETTINGS_REQUEST_CODE);
            return true;
        }
//        else if (id == R.id.action_language) {
//            // Handle language change here
//            showLanguagePopupMenu(findViewById(R.id.action_language));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

//    private void showLanguagePopupMenu(final View view) {
//        PopupMenu popupMenu = new PopupMenu(this, view);
//        MenuInflater inflater = popupMenu.getMenuInflater();
//        inflater.inflate(R.menu.language_popup_menu, popupMenu.getMenu());
//
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//
//                if (id == R.id.menu_item_english) {
//                    setLocale("");
//                    return true;
//                } else if (id == R.id.menu_item_vietnamese) {
//                    setLocale("vi");
//                    return true;
//                }
//                return onOptionsItemSelected(item);
//            }
//        });
//
//        popupMenu.show();
//    }

    //Save last language configuration
    private String getSavedLanguagePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        return sharedPreferences.getString("selectedLanguage", "");
    }
    void saveLanguagePreference(String languageCode) {
        SharedPreferences sharedPreferences = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedLanguage", languageCode);
        editor.apply();
    }
    private String getCurrentLanguage() {
        Configuration configuration = getResources().getConfiguration();
        return configuration.locale.getLanguage();
    }

    //set language
    private void setLocale(String languageCode) {
        if (!getCurrentLanguage().equals(languageCode)) {
        saveLanguagePreference(languageCode);
        Locale newLocale = new Locale(languageCode);
        Locale.setDefault(newLocale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(newLocale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
    }
    private void updateUI() {
        // Refresh UI
        if (titles != null) {
            if (isAPIKeyNotSet())  showAPIKeyInputDialog();
            boolean isCelsius = getSavedTemperatureUnitPreference();
            String dateFormat = getSavedDateFormatPreference();
            String APIkey = getSavedAPIKeyPreference();
            String title = getResources().getString(R.string.app_name);
            setTitle(title);

            for (int i = 0; i < titles.length; i++) {
                titles[i] = getCity(i + 1);
                lat[i] = getLat(i + 1);
                lon[i] = getLon(i + 1);
            }

            PagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), titles, lat, lon, isCelsius, dateFormat, APIkey);
            ViewPager pager = findViewById(R.id.pager);
            pager.setOffscreenPageLimit(3);
            pager.setAdapter(adapter);
        }}

    private boolean getSavedTemperatureUnitPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("pref_temperature_unit", true); // Provide a default value, in case the preference is not set
    }

    private String getSavedDateFormatPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("pref_date_format", "1"); // Provide a default value, in case the preference is not set
    }

    private String getSavedAPIKeyPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("pref_api_key", ""); // Provide a default value, in case the preference is not set
    }
    private boolean isAPIKeyNotSet() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String apiKey = sharedPreferences.getString("pref_api_key", "");
        return apiKey.isEmpty();
    }
    private void showAPIKeyInputDialog() {
        ApiKeyInputDialogFragment apiKeyDialogFragment = new ApiKeyInputDialogFragment();
        apiKeyDialogFragment.show(getSupportFragmentManager(), "API_KEY_DIALOG");
    }

    @Override
    public void onSaveApiKey(String apiKey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pref_api_key", apiKey);
        editor.apply();
        Toast.makeText(this, getString(R.string.APIKeySaved) + " " + apiKey, Toast.LENGTH_SHORT).show();
        updateUI();
    }

    @Override
    public void onDismiss() {
        Toast.makeText(this, getString(R.string.how_ent_API), Toast.LENGTH_SHORT).show();
    }

    private Double getLat(int preferenceNumber) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lat="";
        switch (preferenceNumber) {
            case 1:
                lat = sharedPreferences.getString("pref_lat1", "21.0278"); // Provide a default value, in case the preference is not set
                break;
            case 2:
                lat = sharedPreferences.getString("pref_lat2", "48.8566"); // Provide a default value, in case the preference is not set
                break;
            case 3:
                lat = sharedPreferences.getString("pref_lat3", "43.6047"); // Provide a default value, in case the preference is not set
                break;
        }
        return Double.valueOf(lat);
    }

    private Double getLon(int preferenceNumber) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lon="";
        switch (preferenceNumber) {
            case 1:
                lon= sharedPreferences.getString("pref_lon1", "105.8342"); // Provide a default value, in case the preference is not set
                break;
            case 2:
                lon = sharedPreferences.getString("pref_lon2", "2.3522"); // Provide a default value, in case the preference is not set
                break;
            case 3:
                lon = sharedPreferences.getString("pref_lon3", "1.4442"); // Provide a default value, in case the preference is not set
                break;
        }
        return Double.valueOf(lon);
    }

    private String getCity(int preferenceNumber) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String City="";
        switch (preferenceNumber) {
            case 1:
                City= sharedPreferences.getString("pref_region1", getResources().getString(R.string.HN)); // Provide a default value, in case the preference is not set
                break;
            case 2:
                City = sharedPreferences.getString("pref_region2", getResources().getString(R.string.Pari)); // Provide a default value, in case the preference is not set
                break;
            case 3:
                City = sharedPreferences.getString("pref_region3", getResources().getString(R.string.TL)); // Provide a default value, in case the preference is not set
                break;
        }
        return City;
    }
}
