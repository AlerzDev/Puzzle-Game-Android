package com.alexdev.puzzlegame.activities.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.alexdev.puzzlegame.R;
import com.alexdev.puzzlegame.adapters.ViewPagerAdapter;
import com.alexdev.puzzlegame.fragments.home.HomeFragment;
import com.alexdev.puzzlegame.fragments.home.ProfileFragment;
import com.alexdev.puzzlegame.fragments.home.ScoresFragment;

public class HomeActivity extends AppCompatActivity {

    //Count fragments
    private static final int FRAGMENT_HOME    =  0;
    private static final int FRAGMENT_SCORES  =  1;
    private static final int FRAGMENT_PROFILE =  2;

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
    }

    private void initViews(){
        navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    navigation.getMenu().getItem(0).setChecked(false);
                }

                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment( new HomeFragment());
        adapter.addFragment( new ScoresFragment());
        adapter.addFragment( new ProfileFragment());
        viewPager.setAdapter(adapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(FRAGMENT_HOME);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(FRAGMENT_SCORES);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(FRAGMENT_PROFILE);
                    return true;
            }
            return false;
        }
    };

}
