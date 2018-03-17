package com.example.mjhwa.chat.views;

import android.content.Intent;
import android.support.v4.app.Fragment;

import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mjhwa.chat.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
/*
    //@BindView(R.id.tabs)
    //TabLayout mTabLayout;
    TabLayout mTabLayout;

    //@BindView(R.id.fab)
    //FloatingActionButton mfab;
    FloatingActionButton mfab;

    //@BindView(R.id.viewpager)
    //ViewPager mViewPager;
    ViewPager mViewPager;

    ViewPagerAdapter mpagerAdapter;
    //Button btCalender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
*/
        /*btCalender = (Button) findViewById(R.id.calender);
        btCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalenderActivity.class);
                startActivity(intent);
            }
        });*/
/*
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mfab = (FloatingActionButton) findViewById(R.id.fab);

        mTabLayout.setupWithViewPager(mViewPager);
        setUpViewPager();
        mfab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Fragment currentFragment = mpagerAdapter.getItem(mViewPager.getCurrentItem());
                if(currentFragment instanceof FriendFragment) {
                    ((FriendFragment) currentFragment).toggleSearchBar();
                }
            }
        });
    }

    private void setUpViewPager(){
        mpagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mpagerAdapter.addFragment(new ChatFragment(),  "채팅");
        mpagerAdapter.addFragment(new FriendFragment(), "친구");
        mViewPager.setAdapter(mpagerAdapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }*/
    private ChatFragment chatFragment;
    private FriendFragment friendFragment;
    FloatingActionButton mfab;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        chatFragment = new ChatFragment();
        friendFragment = new FriendFragment();
        mfab = (FloatingActionButton) findViewById(R.id.fab);
        currentFragment = new Fragment();

        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFragment instanceof FriendFragment) {
                    ((FriendFragment) currentFragment).toggleSearchBar();
                }
                else {
                    //친구 탭으로 이동
                }
            }
        });

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if(tabId == R.id.tab_chat) {
                    transaction.replace(R.id.contentContainer, chatFragment).commit();
                    currentFragment = chatFragment;
                }
                else if(tabId == R.id.tab_friend) {
                    transaction.replace(R.id.contentContainer, friendFragment).commit();
                    currentFragment = friendFragment;
                }
            }
        });
    }
}
