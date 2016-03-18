package com.shinado.tagme.main.friends;

import android.content.Context;
import android.widget.Toast;

import com.shinado.tagme.Globals;
import com.shinado.tagme.user.User;

import java.util.ArrayList;
import java.util.HashMap;

import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.utility.Transformable;

public class FriendsLoader implements Downloadable, Transformable{

    @ViewInj
    public ArrayList<User> friends;

    @Override
    public String onDownLoadUrl(Context context) {
        return Globals.URL_MAIN + "get_friends";
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        HashMap<String, String> params = new HashMap<>();
        return params;
    }

    @Override
    public void onError(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public Object fromServer(String fieldName, Object value) {
        if(value == friends){
            return new FriendsPresenter.Wrapper(friends);
        }else{
            return value;
        }
    }

    @Override
    public Object toServer(String fieldName, Object value) {
        return value;
    }
}
