package com.example.imooc_viewpagerindicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.example.view.ViewPagerIndicator;
import com.example.view.ViewPagerIndicator.PageOnChangeListener;

public class MainActivity extends FragmentActivity {
	
	private ViewPager mViewPager;
	private ViewPagerIndicator mIndicator;
	
	private List<String> mTitles = Arrays.asList("短信", "收藏", "推荐",
			"短信1", "收藏1", "推荐1", "短信2", "收藏2", "推荐2");
	private List<VpSimpleFragment> mContents = new ArrayList<VpSimpleFragment>();
	private FragmentPagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		initViews();
		initDatas();
		
		mIndicator.setVisibleTabCount(4);
		mIndicator.setTabItemTitles(mTitles);
		
		mViewPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mViewPager, 0);
	}
		
	private void initDatas() {
		for (String title : mTitles) {
			VpSimpleFragment fragment = VpSimpleFragment.newInstance(title);
			mContents.add(fragment);
		}
		
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				return mContents.size();
			}
			
			@Override
			public Fragment getItem(int arg0) {
				return mContents.get(arg0);
			}
		};
	}

	private void initViews() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mIndicator = (ViewPagerIndicator) findViewById(R.id.indicator);
	}

}
