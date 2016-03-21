package com.shinado.tagme.common;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.shinado.tagme.Globals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import framework.inj.entity.Downloadable;
import framework.inj.entity.Listable;
import provider.Entity;

public class GetMyLikes implements Downloadable{

    public ArrayList<Like> Likes;

    private String myAccount;

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

    @Table(name = "MyLikes")
    public class Like extends Model implements Serializable{
        @Column(name = "tag_id")
        public int tag_id;
    }
}