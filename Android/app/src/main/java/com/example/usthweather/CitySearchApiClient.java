package com.example.usthweather;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CitySearchApiClient {

    private static final String API_ENDPOINT = "https://api.openweathermap.org/geo/1.0/direct";
    private static final int DEFAULT_SEARCH_LIMIT = 5;

    public interface CitySearchListener {
        void onCitySearchResult(String searchResult);
    }

    public void searchCity(Context context, String cityName, String APIKey, CitySearchListener listener) {
        new CitySearchAsyncTask(context, listener).execute(cityName, APIKey);
    }

    private static class CitySearchAsyncTask extends AsyncTask<Object, Void, String> {
        private final CitySearchListener listener;
        private final Context context;

        CitySearchAsyncTask(Context context, CitySearchListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Object... params) {
            String cityName = (String) params[0];
            String APIKey = (String) params[1];

            try {
                // Construct the API URL
                String apiUrl = String.format("%s?q=%s&limit=%d&appid=%s",
                        API_ENDPOINT, cityName, DEFAULT_SEARCH_LIMIT, APIKey);

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
                listener.onCitySearchResult(result);
            }
        }
    }
}
