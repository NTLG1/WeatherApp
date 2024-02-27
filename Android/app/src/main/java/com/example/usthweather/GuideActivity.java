package com.example.usthweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class GuideActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private WelcomePagerAdapter welcomePagerAdapter;
    private LinearLayout dotsLayout;
    private Button btnNext;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_KEY_GUIDE_SHOWN = "isGuideShown";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if the guide has been shown before
        if (isGuideAlreadyShown() && !getIntent().hasExtra("Guide_trigger")) {
            // If the guide has been shown, redirect to the main activity or login screen
            redirectToMainActivity();
            return;
        }

        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.viewPagerGuide);
        dotsLayout = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btn_next);

        // Array of guide layouts
        int[] layouts = {
                R.layout.guide_page1,
                R.layout.guide_page2,
                R.layout.guide_page3
        };

        welcomePagerAdapter = new WelcomePagerAdapter(this, layouts);
        viewPager.setAdapter(welcomePagerAdapter);

        addBottomDots(0);

        // Viewpager page change listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);

                // Change the text of the button if on the last page
                if (position == layouts.length - 1) {
                    btnNext.setText(getString(R.string.start));
                } else {
                    btnNext.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    // Move to the next page
                    viewPager.setCurrentItem(current);
                } else {
                    // The last page, guide has been shown
                    markGuideAsShown();

                    // Redirect to the main activity or login screen
                    redirectToMainActivity();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        // Adding dots at the bottom of the guide pages
        int dotsCount = welcomePagerAdapter.getCount();
        View[] dots = new View[dotsCount];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new View(this);
            dots[i].setBackgroundResource(R.drawable.dot_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.dots_size),
                    getResources().getDimensionPixelSize(R.dimen.dots_size)
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }

        if (dots.length > 0) {
            dots[currentPage].setBackgroundResource(R.drawable.dot_active);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void markGuideAsShown() {
        // Mark the guide as shown by saving a flag in SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREF_KEY_GUIDE_SHOWN, true);
        editor.apply();
    }

    private boolean isGuideAlreadyShown() {
        // Check if the guide has been shown before
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(PREF_KEY_GUIDE_SHOWN, false);
    }

    private void redirectToMainActivity() {
        // Redirect to the main activity or login screen
        Intent intent = new Intent(GuideActivity.this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }
}
