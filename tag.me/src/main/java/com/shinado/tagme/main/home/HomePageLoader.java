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

import framework.inj.ViewValueInj;
import framework.inj.entity.Loadable;
import framework.inj.entity.utility.Transformable;

public class HomePageLoader extends Loadable<Tags> implements Transformable {

    private String account;
    private HashSet<Integer> myLikes = new HashSet<>();
    private HashSet<String> myFollows = new HashSet<>();

    public HomePageLoader(String account, HashSet<Integer> myLikes, HashSet<String> myFollows){
        this.account = account;
        this.myLikes = myLikes;
        this.myFollows = myFollows;
        setEntity(new Tags());
    }

    @ViewValueInj
    public List<Tag> tags(){
        return getEntity().tags;
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
        //TODO what? flag?
        params.put("flag", "1");

        List<Tag> tags = tags();
        if (tags != null && tags.size() > 0){
            id = tags.get(tags.size()-1).id;
        }
        params.put("id", id+"");
        return params;
    }

    @Override
    public void onError(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public Object fromServer(String fieldName, Object value) {
        if(fieldName.equals("tags")){
            return new HomeTagPresenter.Wrapper(tags(), myLikes, myFollows);
        }else{
            return value;
        }
    }

    @Override
    public Object toServer(String fieldName, Object value) {
        return value;
    }

}
