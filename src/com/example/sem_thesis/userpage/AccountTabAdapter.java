package com.example.sem_thesis.userpage;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AccountTabAdapter extends FragmentPagerAdapter {

	// Declare the number of ViewPager pages
	final int PAGE_COUNT = 2;
	private String titles[] = new String[] { "Games I Created", "Games I Played" };

	public AccountTabAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {

			// Open FragmentTab1.java
		case 0:
			ListCreatedGames fragmenttab1 = new ListCreatedGames();
			return fragmenttab1;

			// Open FragmentTab2.java
		case 1:
			ListGamesPlayed fragmenttab2 = new ListGamesPlayed();
			return fragmenttab2;
	
		}
		return null;
	}

	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

}