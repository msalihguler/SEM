package com.example.sem_thesis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {

	// Declare the number of ViewPager pages
	final int PAGE_COUNT = 2;
	private String titles[] = new String[] { "Find to See", "Trekking on the Route" };

	public TabAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {

			// Open FragmentTab1.java
		case 0:
			ShowAllFindToSeeOnMap fragmenttab1 = new ShowAllFindToSeeOnMap();
			return fragmenttab1;

			// Open FragmentTab2.java
		case 1:

			ShowAllTrekkingOnTheRoute fragmenttab2 = new ShowAllTrekkingOnTheRoute();
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