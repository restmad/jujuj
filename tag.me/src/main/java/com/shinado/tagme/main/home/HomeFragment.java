package com.shinado.tagme.main.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinado.tagme.R;

import framework.core.Jujuj;

public class HomeFragment extends Fragment {

    private HomePageLoader mHomePage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mHomePage = new HomePageLoader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Jujuj.getInstance().inject(getActivity(), view, mHomePage);
        return view;
    }
}
