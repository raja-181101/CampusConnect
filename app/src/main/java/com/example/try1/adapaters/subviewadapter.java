package com.example.try1.adapaters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.try1.fragments.post;
import com.example.try1.fragments.questions;
import com.example.try1.fragments.subjects;
import com.example.try1.fragments.subjectsEnrolled;

public class subviewadapter extends FragmentStateAdapter {
    public subviewadapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new subjectsEnrolled();
            case 1:
                return new subjects();
            default:
                return new subjectsEnrolled();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
