package com.android.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.music.R;
import com.android.music.fragment.FolderFragment;
import com.android.music.fragment.SingleFragment;
import com.android.music.util.Constant;
import com.android.music.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地音乐 具体逻辑在fragment中
 */
public class LocalMusicActivity extends PlayBarBaseActivity {

    private static final String TAG = LocalMusicActivity.class.getName();
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private MyViewPager viewPager;
    private MyAdapter fragmentAdapter;
    private List<String> titleList = new ArrayList<>(4);
    private List<Fragment> fragments = new ArrayList<>(4);
    private SingleFragment singleFragment;


    private FolderFragment folderFragment;
    private TextView nothingTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        toolbar = (Toolbar)findViewById(R.id.local_music_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Constant.LABEL_LOCAL);
        }
        init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " );
    }


    private void init(){
        addTapData();
        viewPager = (MyViewPager)findViewById(R.id.local_viewPager);
        tabLayout = (TabLayout)findViewById(R.id.local_tab);
        fragmentAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(2); //预加载页面数
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        nothingTv = (TextView)findViewById(R.id.local_nothing_tv);
        nothingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalMusicActivity.this,ScanActivity.class);
                startActivity(intent);
            }
        });

    }



    private void addTapData() {
        titleList.add("");


        if (singleFragment == null) {
            singleFragment = new SingleFragment();
            fragments.add(singleFragment);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.local_music_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.scan_local_menu){
            Intent intent = new Intent(LocalMusicActivity.this,ScanActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return true;
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * 用来显示tab上的名字
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

}
