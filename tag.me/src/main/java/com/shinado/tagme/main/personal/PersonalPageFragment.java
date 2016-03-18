package com.shinado.tagme.main.personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinado.tagme.R;

import framework.core.Jujuj;

public class PersonalPageFragment extends Fragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_personal_page, container, false);
        Jujuj.getInstance().inject(getActivity(), view, new PersonalPageLoader());
        return view;
    }
}
