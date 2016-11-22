package nl.han.asd.project.client.android.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BootActivityPagerAdapter extends FragmentPagerAdapter {
    private static final String[] FRAGMENT_TITLES = new String[]{"Login", "Register"};
    private final Fragment[] fragments;

    public BootActivityPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new Fragment[]{new LoginFragment_(), new RegisterFragment_()};
    }

    @Override
    public Fragment getItem(int i) {
        return fragments[i];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return FRAGMENT_TITLES[position];
    }
}
