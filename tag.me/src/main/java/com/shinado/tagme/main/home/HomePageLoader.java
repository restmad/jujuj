package com.shinado.tagme.main.home;

import android.content.Context;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.shinado.tagme.Globals;
import com.shinado.tagme.URLs;
import com.shinado.tagme.entity.Tag;
import com.shinado.tagme.entity.Tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import framework.inj.ViewValueInj;
import framework.inj.entity.Loadable;
import framework.inj.entity.utility.Transformable;

public class HomePageLoader extends Loadable<Tags> {

    private final int FLAG_REFRESH = 1;
    private final int FLAG_MORE = 2;

    private int flag = FLAG_REFRESH;
    private String account;
    private HashSet<Integer> myLikes = new HashSet<>();
    private HashSet<String> myFollows = new HashSet<>();

    public HomePageLoader(String account, HashSet<Integer> myLikes, HashSet<String> myFollows) {
        this.account = account;
        this.myLikes = myLikes;
        this.myFollows = myFollows;
        setEntity(new Tags());
    }

    @ViewValueInj
    public HomeTagPresenter.Wrapper tags() {
        return new HomeTagPresenter.Wrapper(getEntity().tags, myLikes, myFollows);
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return Globals.URL_TAG_ME + URLs.GET_TAGS;
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        int id = 0;
        HashMap<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("flag", "" + flag);
        TreeSet<Tag> tags = (TreeSet<Tag>) tags().getList();
        if (tags != null && tags.size() > 0) {
            if (flag == FLAG_REFRESH) {
                id = tags.first().id;
            } else {
                id = tags.last().id;
            }
        }else{
            if (flag == FLAG_MORE){
                id = Integer.MAX_VALUE;
            }
        }

        params.put("id", id + "");
        return params;
    }

    @Override
    public void onError(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
