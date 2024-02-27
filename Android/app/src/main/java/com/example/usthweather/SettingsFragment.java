package com.example.usthweather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {
    private CitySearchApiClient citySearchApiClient;
    private CountryApiClient countryApiClient;
    private ProgressDialog progressDialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Your existing preference code here
        setPreferencesFromResource(R.xml.preferences, rootKey);
        // Check if API key is empty
        if (isAPIKeyNotSet())  getActivity().setResult(Activity.RESULT_OK);
        // Find and handle the language preference
        ListPreference languagePreference = findPreference("pref_app_language");
        if (languagePreference != null) {
            // Set the default value
            String defaultValue = getCurrentLanguage();
            languagePreference.setValue(defaultValue);

            languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Handle the language preference change event here
                    String selectedLanguage = (String) newValue;
                    setLocale(selectedLanguage);
                    return true;
                }
            });
        }

        // Find and handle the temperature unit format preference
        SwitchPreference temperatureUnitPreference = findPreference("pref_temperature_unit");
        if (temperatureUnitPreference != null) {
            temperatureUnitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Handle the temperature unit preference change event here
                    boolean isCelsius = (boolean) newValue;
                    saveTemperatureUnitPreference(isCelsius);
                    // Set result to indicate that the temperature unit has changed
                    getActivity().setResult(Activity.RESULT_OK);
                    return true;
                }
            });
        }

        // Find and handle the date format preference
        ListPreference dateFormatPreference = findPreference("pref_date_format");
        if (dateFormatPreference != null) {
            dateFormatPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Handle the date format preference change event here
                    String selectedDateFormat = (String) newValue;
                    saveDateFormatPreference(selectedDateFormat);
                    // Set result to indicate that the date format has changed
                    getActivity().setResult(Activity.RESULT_OK);
                    return true;
                }
            });
        }

        // Find and handle the API key preference
        Preference apiKeyPreference = findPreference("pref_api_key");
        if (apiKeyPreference != null) {
            apiKeyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Handle the API key preference change event here
                    String apiKey = (String) newValue;
                    saveApiKeyPreference(apiKey);
                    // Set result to indicate that the api key has changed
                    getActivity().setResult(Activity.RESULT_OK);
                    return true;
                }
            });
        }

        // Trigger Guide Screen
        Preference guidePreference = findPreference("pref_guide");
        guidePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), GuideActivity.class);
                intent.putExtra("Guide_trigger", true);
                startActivity(intent);
                return true;
            }
        });

        // City preference
        String city_backup1 = getCity(1);
        findPreference("pref_region1").setSummary(city_backup1);
        Preference cityPreference1 = findPreference("pref_region1");
        if (cityPreference1 != null) {
            cityPreference1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Handle the API key preference change event here
                    String city = (String) newValue;
                    searchCityApi(city_backup1, city, getSavedAPIKeyPreference(), 1);
                    return true;
                }
            });
        }
        String city_backup2 = getCity(2);
        findPreference("pref_region2").setSummary(city_backup2);
        Preference cityPreference2 = findPreference("pref_region2");
        if (cityPreference2 != null) {
            cityPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Handle the API key preference change event here
                    String city = (String) newValue;
                    searchCityApi(city_backup2, city, getSavedAPIKeyPreference(), 2);
                    return true;
                }
            });
        }
        String city_backup3 = getCity(3);
        findPreference("pref_region3").setSummary(city_backup3);
        Preference cityPreference3 = findPreference("pref_region3");
        if (cityPreference3 != null) {
            cityPreference3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Handle the API key preference change event here
                    String city = (String) newValue;
                    searchCityApi(city_backup3, city, getSavedAPIKeyPreference(), 3);
                    return true;
                }
            });
        }

        // Find and handle the "About" preference
        Preference aboutPreference = findPreference("pref_about");
        if (aboutPreference != null) {
            aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Handle the "About" preference click event here
                    // For example, show an About dialog
                    showAboutDialog();
                    return true;
                }
            });
        }
    }

    private void saveTemperatureUnitPreference(boolean isCelsius) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_temperature_unit", isCelsius);
        editor.apply();
    }

    private void saveDateFormatPreference(String selectedDateFormat) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pref_date_format", selectedDateFormat);
        editor.apply();
    }

    private void saveApiKeyPreference(String apiKey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pref_api_key", apiKey);
        editor.apply();
    }

    private String getSavedAPIKeyPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getString("pref_api_key", ""); // Provide a default value, in case the preference is not set
    }

    private boolean isAPIKeyNotSet() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String apiKey = sharedPreferences.getString("pref_api_key", "");
        return apiKey.isEmpty();
    }

    private void setLocale(String languageCode) {
        if (!getCurrentLanguage().equals(languageCode)) {
            Intent weatherActivity = new Intent(getContext(), WeatherActivity.class);
            weatherActivity.putExtra("LangChanged", languageCode);
            startActivity(weatherActivity);
        }
    }

    private String getCurrentLanguage() {
        Configuration configuration = getResources().getConfiguration();
        return configuration.locale.getLanguage();
    }

    private void searchCityApi(String cityBackup, String cityName, String APIKey, int preferenceNumber) {
        citySearchApiClient = new CitySearchApiClient();
        try {
            // Get city data
            citySearchApiClient.searchCity(requireContext(), cityName, APIKey, new CitySearchApiClient.CitySearchListener() {
                @Override
                public void onCitySearchResult(String cityData) {
                    if (isAdded()) { // Check if fragment is still attached
                        if (cityData != null) {
                            try {
                                // Parse JSON response
                                JSONArray citiesArray = new JSONArray(cityData);
                                if (citiesArray == null || citiesArray.length() == 0) {
                                    if (isAdded()) {
                                        showNoResultDialog();
                                        updateCityPreference(cityBackup, preferenceNumber, "", "");
                                    }
                                } else {
                                    // Extract city names
                                    final String[] cityNames = new String[citiesArray.length()];
                                    final String[] lat = new String[citiesArray.length()];
                                    final String[] lon = new String[citiesArray.length()];
                                    final String[] withCountryNames = new String[citiesArray.length()];
                                    final int[] citiesProcessed = {0}; // Array of size 1 to make it effectively final

                                    // Define a callback for country search
                                    CountryApiClient.CountrySearchCallback countrySearchCallback = new CountryApiClient.CountrySearchCallback() {
                                        @Override
                                        public void onCountrySearchComplete(String commonName, String flag) {
                                            // Handle the country search result
                                            citiesProcessed[0]++;
                                            // Add city name with country info to the array
                                            withCountryNames[citiesProcessed[0] - 1] = cityNames[citiesProcessed[0] - 1] + " - " + commonName + " " + flag;

                                            // If all cities processed, show the dialog
                                            if (citiesProcessed[0] == citiesArray.length()) {
                                                if (isAdded()) {
                                                    dismissPleaseWaitDialog(); // Dismiss the "please wait" dialog
                                                    showCitySelectionDialog(cityNames, withCountryNames, preferenceNumber, lat, lon);
                                                }
                                            }
                                        }
                                    };

                                    // Show "Please Wait" dialog
                                    showPleaseWaitDialog();

                                    // Iterate over cities and initiate country search for each
                                    for (int i = 0; i < citiesArray.length(); i++) {
                                        JSONObject cityObject = citiesArray.getJSONObject(i);
                                        String countryCode = cityObject.getString("country");
                                        searchCountry(countryCode, countrySearchCallback);

                                        String localName = getLocalizedName(cityObject);
                                        cityNames[i] = localName.isEmpty() ? cityObject.getString("name") : localName;
                                        lat[i] = cityObject.getString("lat");
                                        lon[i] = cityObject.getString("lon");
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void searchCountry(String countryCode, CountryApiClient.CountrySearchCallback callback) {
        countryApiClient = new CountryApiClient();
        try {
            countryApiClient.searchCountry(requireContext(), countryCode, new CountryApiClient.CountrySearchListener() {
                @Override
                public void onCountrySearchResult(String searchResult) {
                    if (searchResult != null) {
                        try {
                            // Parse JSON response
                            JSONArray countryArray = new JSONArray(searchResult);
                            JSONObject countryObject = countryArray.getJSONObject(0);
                            JSONObject nameObject = countryObject.getJSONObject("name");
                            String flag = countryObject.getString("flag");
                            String commonName = nameObject.getString("common");

                            // Invoke the callback with the country details
                            callback.onCountrySearchComplete(commonName, flag);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLocalizedName(JSONObject cityObject) throws JSONException {
        if (cityObject.has("local_names")) {
            JSONObject localNames = cityObject.getJSONObject("local_names");
            String currentLanguage = getCurrentLanguage();

            // Check if the local name exists and has the same language as the current language
            if (localNames.has(currentLanguage)) {
                return localNames.getString(currentLanguage);
            }
        }
        return "";
    }


    private void showCitySelectionDialog(String[] cityNames, String[] withCountryNames, int preferenceNumber, String[] lat, String[] lon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.Select_a_city))
                .setItems(withCountryNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User selected a city
                        String selectedCity = cityNames[which];
                        String selectedLat = lat[which];
                        String selectedLon = lon[which];
                        // Update the corresponding preference based on preferenceNumber
                        updateCityPreference(selectedCity, preferenceNumber, selectedLat, selectedLon);
                    }
                });
        builder.create().show();
    }

    private void showNoResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(getResources().getString(R.string.no_result))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle the OK button click if needed
                    }
                });
        builder.create().show();
    }

    private void updateCityPreference(String selectedCity, int preferenceNumber, String lat, String lon) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Update the corresponding preference based on preferenceNumber
        switch (preferenceNumber) {
            case 1:
                editor.putString("pref_region1", selectedCity);
                if (lat.length() != 0 && lon.length()!=0) {
                    findPreference("pref_region1").setSummary(selectedCity);
                    editor.putString("pref_lat1", lat);
                    editor.putString("pref_lon1", lon);
                }
                break;
            case 2:
                editor.putString("pref_region2", selectedCity);
                if (lat.length() != 0 && lon.length()!=0) {
                    findPreference("pref_region2").setSummary(selectedCity);
                    editor.putString("pref_lat2", lat);
                    editor.putString("pref_lon2", lon);
                }
                break;
            case 3:
                editor.putString("pref_region3", selectedCity);
                if (lat.length() != 0 && lon.length()!=0) {
                    findPreference("pref_region3").setSummary(selectedCity);
                    editor.putString("pref_lat3", lat);
                    editor.putString("pref_lon3", lon);
                }
                break;
        }
        editor.apply();
        if (lat.length() != 0 && lon.length()!=0) {
            // Set result to indicate that the city preference has changed
            getActivity().setResult(Activity.RESULT_OK);
        }
    }

    private String getCity(int preferenceNumber) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
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

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.about));
        // Build the message with the project description and status
        String aboutMessage = getResources().getString(R.string.about_message);

        builder.setMessage(aboutMessage);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showPleaseWaitDialog() {
        // Create a new ProgressDialog
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage(getResources().getString(R.string.wait));
        progressDialog.setCancelable(false); // To prevent users from canceling the dialog
        progressDialog.show();
    }

    private void dismissPleaseWaitDialog() {
        // Dismiss the ProgressDialog if it's showing
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

