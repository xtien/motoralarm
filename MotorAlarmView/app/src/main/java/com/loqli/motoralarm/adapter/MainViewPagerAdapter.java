package com.loqli.motoralarm.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> fragments;

	public MainViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
		super(fragmentManager);
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	public void push(Fragment fragment) {
		fragments.add(fragment);
	}

	public void pop() {

		fragments.remove(fragments.size() - 1);
		notifyDataSetChanged();
	}
}
