package com.shinado.tagme.main.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinado.tagme.R;
import com.shinado.tagme.common.UserPref;
import com.shinado.tagme.main.MainActivity;

import java.util.HashSet;

import framework.core.Jujuj;

public class HomeFragment extends Fragment {

    private HomePageLoader mHomePage;
    private View mView;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mHomePage = new HomePageLoader(new UserPref(getContext()).getUesrAccount(),
                (HashSet<Integer>) bundle.getSerializable(MainActivity.EXTRA_MY_LIKES),
                (HashSet<String>) bundle.getSerializable(MainActivity.EXTRA_MY_FOLLOWERS)
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        loadMore();
        return mView;
    }

    public void loadMore(){
        Jujuj.getInstance().inject(getActivity(), mView, mHomePage);
    }
}
