package com.salihttnc.myapplication.Adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

  private final List<Fragment> fragmentList=new ArrayList<>();
  private final List<String> fragmentTitle=new ArrayList<>();

  public ViewPagerAdapter(FragmentManager fm) {
    super(fm);
  }


  @Override
  public Fragment getItem(int i) {
    return fragmentList.get(i);
  }

  @Override
  public int getCount() {
    return fragmentTitle.size();
  }
  public void addFragment(Fragment fragment, String title){

    fragmentList.add(fragment);
    fragmentTitle.add(title);


  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return fragmentTitle.get(position);

  }
}
