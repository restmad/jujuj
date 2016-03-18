package com.shinado.tagme.main.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shinado.tagme.R;

import framework.core.Jujuj;

public class FriendsFragment extends Fragment{

    private FriendsLoader mFriendsLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriendsLoader = new FriendsLoader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        ListView listView = (ListView) view.findViewById(R.id.friends);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
            }
        });
        Jujuj.getInstance().inject(getActivity(), view, mFriendsLoader);
        return view;
    }


}
