//package com.example.usthweather;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//
//public class WeatherAndForecastFragment extends Fragment {
//
//    @Override
//    public View onCreateView(
//            LayoutInflater inflater, ViewGroup forecast,
//            Bundle savedInstanceState
//    ) {
//        String[] dayNames = getResources().getStringArray(R.array.day_names);
//        int[] iconResourceIds = {R.drawable.cloudy_1, R.drawable.sunny, R.drawable.rainy_1,
//                R.drawable.stormy, R.drawable.cloudy_sun, R.drawable.sunny, R.drawable.sunny};
//        String[] Status = getResources().getStringArray(R.array.day_statuses);
//        // Inflate the fragment layout
//        View view = inflater.inflate(R.layout.fragment_forecast, forecast, false);
//
//        RelativeLayout relativeLayout = view.findViewById(R.id.weatherFragment);
//        View weatherlayout = inflater.inflate(R.layout.fragment_weather, forecast, false);
//
//        TextView dayStatusMain = weatherlayout.findViewById(R.id.dayStatusMain);
//        dayStatusMain.setText(Status[0]);
//
//        ImageView weatherIconImageViewMain = weatherlayout.findViewById(R.id.weatherIconImageViewMain);
//        weatherIconImageViewMain.setImageResource(iconResourceIds[0]);
//
//        TextView city = weatherlayout.findViewById(R.id.city);
//        city.setText(getString(R.string.city_name));
//
//        relativeLayout.addView(weatherlayout);
//        LinearLayout linearLayout = view.findViewById(R.id.forecastLinearLayout);
//
//        for (int i = 0; i < dayNames.length; i++) {
//            View dayForecastView = inflater.inflate(R.layout.item_forecast_day, linearLayout, false);
//
//            TextView dayTextView = dayForecastView.findViewById(R.id.dayTextView);
//            dayTextView.setText(dayNames[i]);
//
//            ImageView weatherIconImageView = dayForecastView.findViewById(R.id.weatherIcon);
//            weatherIconImageView.setImageResource(iconResourceIds[i]);
//
//            TextView dayStatus = dayForecastView.findViewById(R.id.dayStatus);
//            dayStatus.setText(Status[i]);
//
//            // Add the day's forecast item to the main layout
//            linearLayout.addView(dayForecastView);
//        }
//
//        return view;
//    }
//}
package com.example.usthweather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class WeatherAndForecastFragment extends Fragment {

    private WeatherApiClient weatherApiClient;
    private ForecastApiClient forecastApiClient;
    Double lat;
    Double lon;
    Boolean isCelsius;
    String date_Format;
    String APIKey;
    public WeatherAndForecastFragment(double lat, double lon, boolean isCelsius, String date_Format, String APIKey) {
        this.lat = lat;
        this.lon = lon;
        this.isCelsius = isCelsius;
        this.date_Format = date_Format;
        this.APIKey = APIKey;
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup forecast,
            Bundle savedInstanceState
    ) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_forecast, forecast, false);

        RelativeLayout relativeLayout = view.findViewById(R.id.weatherFragment);
        View weatherlayout = inflater.inflate(R.layout.fragment_weather, forecast, false);
        relativeLayout.addView(weatherlayout);

        weatherApiClient = new WeatherApiClient();
        forecastApiClient = new ForecastApiClient();

        try {
            // Get weather data
            weatherApiClient.getWeatherData(requireContext(), lat, lon, APIKey, new WeatherApiClient.WeatherDataListener() {
                @Override
                public void onWeatherDataReceived(String weatherData) {
                    if (weatherData != null) {
                        try {
                            // Parse JSON response
                            JSONObject WeatherJson = new JSONObject(weatherData);

                            // Update UI with Hanoi weather information
                            updateWeatherUI(WeatherJson, weatherlayout);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(APIKey.length()!=0)
                        Toast.makeText(getActivity(), getString(R.string.con_fail), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Get forecast data
            forecastApiClient.getForecastData(requireContext(), lat, lon, APIKey, new ForecastApiClient.ForecastDataListener() {
                @Override
                public void onForecastDataReceived(String forecastData) {
                    if (forecastData != null) {
                        try {
                            // Parse JSON response
                            JSONObject ForecastJson = new JSONObject(forecastData);

                            // Update UI with Hanoi weather information
                            updateForecastUI(ForecastJson);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void updateWeatherUI(JSONObject weatherJson, View weatherLayout) throws JSONException {
        // Extract relevant information from JSON response
        String cityName = weatherJson.optString("name");
        String mainWeatherStatus = weatherJson.getJSONArray("weather").getJSONObject(0).optString("main");
        int weatherIconResource = getWeatherIconResource(mainWeatherStatus);
        JSONObject windObject = weatherJson.getJSONObject("wind");
        JSONObject mainJsonObject = weatherJson.getJSONObject("main");
        double tempKelvin = mainJsonObject.optDouble("temp");
        double tempKelvin_min = mainJsonObject.optDouble("temp_min");
        double tempKelvin_max = mainJsonObject.optDouble("temp_max");
        double humidity =  mainJsonObject.optDouble("humidity");
        double pressure =  mainJsonObject.optDouble("pressure");
        double wind =  windObject.optDouble("speed");
        wind = wind * 3.6;
        double tempCelsius = kelvinToCelsius(tempKelvin);
        double tempCelsius_min = kelvinToCelsius(tempKelvin_min);
        double tempCelsius_max = kelvinToCelsius(tempKelvin_max);
        double tempFahrenheit = celsiusToFahrenheit(tempCelsius);
        double tempFahrenheit_min = celsiusToFahrenheit(tempCelsius_min);
        double tempFahrenheit_max = celsiusToFahrenheit(tempCelsius_max);
        String descriptionWeatherStatus = capitalizeFirstLetterEachWord(weatherJson.getJSONArray("weather").getJSONObject(0).optString("description"));
        // Update UI elements
        TextView cityTextView = weatherLayout.findViewById(R.id.city);
        cityTextView.setText(cityName);

        TextView dayStatusMain = weatherLayout.findViewById(R.id.dayStatusMain);
        TextView dayStatusMain2 = weatherLayout.findViewById(R.id.dayStatusMain2);
        TextView dayStatusMain3 = weatherLayout.findViewById(R.id.dayStatusMain3);
        if (isCelsius) {
            dayStatusMain.setText(String.format("%s", descriptionWeatherStatus));
            dayStatusMain2.setText(String.format("\uD83C\uDF21 %.1f°C\n%s: %.1f°C\n%s: %.1f°C", tempCelsius, getResources().getString(R.string.min), tempCelsius_min, getResources().getString(R.string.max), tempCelsius_max));
            dayStatusMain3.setText(String.format("%s: %.0f %%\n%s: %.2f km/h\n%s: %.0f hPa", getResources().getString(R.string.humidity), humidity, getResources().getString(R.string.wind), wind, getResources().getString(R.string.pressure), pressure));
        } else {
            dayStatusMain.setText(String.format("%s", descriptionWeatherStatus));
            dayStatusMain2.setText(String.format("\uD83C\uDF21 %.1f°F\n%s: %.1f°F\n%s: %.1f°F", tempFahrenheit, getResources().getString(R.string.min), tempFahrenheit_min, getResources().getString(R.string.max), tempFahrenheit_max));
            dayStatusMain3.setText(String.format("%s: %.0f %%\n%s: %.2f km/h\n%s: %.0f hPa", getResources().getString(R.string.humidity), humidity, getResources().getString(R.string.wind), wind, getResources().getString(R.string.pressure), pressure));
        }

        ImageView weatherIconImageViewMain = weatherLayout.findViewById(R.id.weatherIconImageViewMain);
        weatherIconImageViewMain.setImageResource(weatherIconResource);
    }
    // Forecast UI part
    private void updateForecastUI(JSONObject forecastJson) throws JSONException {
        // Extract the "list" JSONArray from the forecastJson
        JSONArray forecastList = forecastJson.getJSONArray("list");
        String[] dayNames = getResources().getStringArray(R.array.day_names);

        // Iterate through each forecast item
        for (int i = 0; i < forecastList.length(); i++) {
            // Get the forecast item at index i
            JSONObject forecastItem = forecastList.getJSONObject(i);

            // Extract relevant information from the forecast item
            long timestamp = forecastItem.getLong("dt") * 1000; // Convert timestamp to milliseconds
            String date = formatDate(timestamp); // Format the date

            JSONObject mainJsonObject = forecastItem.getJSONObject("main");
            JSONObject windObject = forecastItem.getJSONObject("wind");
            double tempKelvin = mainJsonObject.optDouble("temp");
            double tempKelvin_min = mainJsonObject.optDouble("temp_min");
            double tempKelvin_max = mainJsonObject.optDouble("temp_max");
            double humidity =  mainJsonObject.optDouble("humidity");
            double pressure =  mainJsonObject.optDouble("pressure");
            double wind =  windObject.optDouble("speed");
            wind = wind * 3.6;
            double tempCelsius = kelvinToCelsius(tempKelvin);
            double tempCelsius_min = kelvinToCelsius(tempKelvin_min);
            double tempCelsius_max = kelvinToCelsius(tempKelvin_max);
            double tempFahrenheit = celsiusToFahrenheit(tempCelsius);
            double tempFahrenheit_min = celsiusToFahrenheit(tempCelsius_min);
            double tempFahrenheit_max = celsiusToFahrenheit(tempCelsius_max);
            String weatherStatus = forecastItem.getJSONArray("weather").getJSONObject(0).optString("main");
            int weatherIconResource = getWeatherIconResource(weatherStatus);
            String describeWeatherStatus = forecastItem.getJSONArray("weather").getJSONObject(0).optString("description");
            describeWeatherStatus = capitalizeFirstLetterEachWord(describeWeatherStatus);

            // Update UI elements for each forecast item
            View forecastItemView = createForecastItemView(dayNames[dayOfWeek(timestamp)], date, describeWeatherStatus, tempCelsius, tempCelsius_min, tempCelsius_max, tempFahrenheit, tempFahrenheit_min, tempFahrenheit_max, humidity, wind, pressure, weatherIconResource);
            LinearLayout forecastLinearLayout = getView().findViewById(R.id.forecastLinearLayout);
            forecastLinearLayout.addView(forecastItemView);
        }
    }

    private String formatDate(long timestamp) {
        return String.valueOf(timestamp);
    }

    private int dayOfWeek(long timestamp) {
        Date date = new Date(timestamp);
        // Create a Calendar instance and set the time to the date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Get the day of the week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek-1;
    }

    private View createForecastItemView(String dayName, String date, String describeWeatherStatus, double tempCelsius, double tempCelsius_min, double tempCelsius_max, double tempFahrenheit, double tempFahrenheit_min, double tempFahrenheit_max, double humidity, double wind, double pressure, int weatherIconResource) {
        // Inflate the forecast item layout
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View forecastItemView = inflater.inflate(R.layout.item_forecast_day, (ViewGroup) getView(), false);

        // Find UI elements in the forecast item layout
        TextView dayTextView = forecastItemView.findViewById(R.id.dayTextView);
        TextView dayTextView2 = forecastItemView.findViewById(R.id.dayTextView2);
        ImageView weatherIconImageView = forecastItemView.findViewById(R.id.weatherIcon);
        TextView dayStatus = forecastItemView.findViewById(R.id.dayStatus);

        // Format the date
        String formattedDate = formatDateString(date);
        String time = formattedDate.substring(13,18);
        String Date = formattedDate.substring(0,10);
        String Date_Time = String.format("%s\n     %s", Date, time);

        // Update UI elements with forecast data
        dayTextView.setText(dayName);
        dayTextView2.setText(Date_Time);
        weatherIconImageView.setImageResource(weatherIconResource);
        if (isCelsius)
            dayStatus.setText(String.format("%s\n\uD83C\uDF21 %.1f°C   %.1f°C - %.1f°C\n%s: %.0f %%\n%s: %.2f km/h\n%s: %.0f hPa", describeWeatherStatus, tempCelsius, tempCelsius_min, tempCelsius_max, getResources().getString(R.string.humidity), humidity, getResources().getString(R.string.wind), wind, getResources().getString(R.string.pressure), pressure));
        else
            dayStatus.setText(String.format("%s\n\uD83C\uDF21 %.1f°F   %.1f°F - %.1f°F\n%s: %.0f %%\n%s: %.2f km/h\n%s: %.0f hPa", describeWeatherStatus, tempFahrenheit, tempFahrenheit_min, tempFahrenheit_max, getResources().getString(R.string.humidity), humidity, getResources().getString(R.string.wind), wind, getResources().getString(R.string.pressure), pressure));
        return forecastItemView;
    }


    private int getWeatherIconResource(String weatherStatus) {
        // Logic to map weather status to corresponding drawable resources
        switch (weatherStatus) {
            case "Rain":
                return R.drawable.rainy_1;
            case "Clear":
                return R.drawable.sunny;
            case "Clouds":
                return R.drawable.cloudy_1;
            case "Snow":
                return R.drawable.snowy;
            case "Thunderstorm":
                return R.drawable.stormy;
            default:
                return R.drawable.sunny;
        }
    }
    private double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15; // Conversion formula from Kelvin to Celsius
    }
    private double celsiusToFahrenheit(double celsius) {
        return celsius * 9 / 5 + 32; // Conversion formula from Celsius to Fahrenheit
    }
    private String capitalizeFirstLetterEachWord(String input) {
        StringBuilder result = new StringBuilder();
        String[] words = input.split(" ");

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim();
    }
    private String formatDateString(String timestamp) {
        // Convert timestamp (in milliseconds) to Date object
        Date date = new Date(Long.parseLong(timestamp));
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy   HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy   HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd   HH:mm", Locale.getDefault());

        if (date_Format.equals("1"))
            // Format the date as "DD/MM/YYYY HH:MM"
            return dateFormat1.format(date);
        else if (date_Format.equals("2"))
            // Format the date as "MM/DD/YYYY HH:mm"
            return dateFormat2.format(date);
        else
            // Format the date as "YYYY/MM/DD HH:mm"
            return dateFormat3.format(date);
    }
}

