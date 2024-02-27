package com.example.usthweather;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CountryApiClient {

    private static final String API_ENDPOINT = "https://restcountries.com/v3.1/alpha?codes=";

    public interface CountrySearchListener {
        void onCountrySearchResult(String searchResult);
    }

    public void searchCountry(Context context, String countryCode, CountrySearchListener listener) {
        new CountrySearchAsyncTask(context, listener).execute(countryCode);
    }

    public interface CountrySearchCallback {
        void onCountrySearchComplete(String commonName, String flag);
    }

    private static class CountrySearchAsyncTask extends AsyncTask<Object, Void, String> {
        private final CountrySearchListener listener;
        private final Context context;

        CountrySearchAsyncTask(Context context, CountrySearchListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Object... params) {
            String countryCode = (String) params[0];

            try {
                // Construct the API URL
                String apiUrl = API_ENDPOINT + countryCode;

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (listener != null) {
                listener.onCountrySearchResult(result);
            }
        }
    }
}

