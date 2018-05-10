package com.example.william.speechpad;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.ListView;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titulos[];//Guarda os títulos de cada TAB
    int NumTabs;//Guarda os números das TABS
    Context context;

    public ViewPagerAdapter(Context mContext, FragmentManager fm, CharSequence mTitulos[], int mNumTabs) {
        super(fm);
        this.Titulos = mTitulos;
        this.NumTabs = mNumTabs;
        this.context = mContext;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)//Primeira Tab
        {
            Tab1 tab1 = new Tab1(context);
            return tab1;
        } else if (position == 1) {
            Tab2 tab2 = new Tab2(context);
            return tab2;
        } else {
            Tab3 tab3 = new Tab3(context);
            return tab3;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titulos[position];
    }

    @Override
    public int getCount() {
        return NumTabs;
    }
}