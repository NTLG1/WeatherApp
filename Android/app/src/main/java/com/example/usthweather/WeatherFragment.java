package com.example.usthweather;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.view.View;

public class WeatherFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup weatherFragment,
            Bundle savedInstanceState
    ) {
        // Inflate the fragment layout for WeatherFragment
        View view = inflater.inflate(R.layout.fragment_weather, weatherFragment, false);
        view.setBackgroundColor(Color.parseColor("#d8fcdc"));
        return view;
    }
}