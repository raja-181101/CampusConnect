package com.example.try1.adapaters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.try1.fragments.post;
import com.example.try1.fragments.questions;

import java.util.ArrayList;

public class viewpageradapter extends FragmentStateAdapter {

    public viewpageradapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new post();
            case 1:
                return new questions();
            default:
                return new post();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
