package com.example.usthweather;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApiClient {

    private static final String API_ENDPOINT = "https://api.openweathermap.org/data/2.5/weather";

    public interface WeatherDataListener {
        void onWeatherDataReceived(String weatherData);
    }

    public void getWeatherData(Context context, double lat, double lon, String APIKey, WeatherDataListener listener) {
        new WeatherDataAsyncTask(context, listener).execute(lat, lon, APIKey);
    }

    private static class WeatherDataAsyncTask extends AsyncTask<Object, Void, String> {
        private final WeatherDataListener listener;
        private final Context context;

        WeatherDataAsyncTask(Context context, WeatherDataListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Object... params) {
            double lat = (double) params[0];
            double lon = (double) params[1];
            String APIKey = (String) params[2];

            // Get current language from context
            String currentLanguage = getCurrentLanguage();

            try {
                String apiUrl = String.format("%s?lat=%f&lon=%f&appid=%s&lang=%s", API_ENDPOINT, lat, lon, APIKey, currentLanguage);

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
                listener.onWeatherDataReceived(result);
            }
        }

        // Helper method to get the current language from the context
        private String getCurrentLanguage() {
            Configuration configuration = context.getResources().getConfiguration();
            return configuration.locale.getLanguage();
        }
    }
}
