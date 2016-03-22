package com.shinado.tagme.common;

import android.content.Context;

import com.shinado.tagme.Globals;

import java.util.ArrayList;
import java.util.HashMap;

import framework.inj.entity.Downloadable;

public class GetMyLikes implements Downloadable{

    public ArrayList<Like> Likes;

    private String myAccount;

    public GetMyLikes(){

    }

    public GetMyLikes(String account){
        this.myAccount = account;
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return Globals.URL_TAG_ME + "get_my_likes.php";
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("account", myAccount);
        return params;
    }

    @Override
    public void onError(Context context, String msg) {

    }

}
