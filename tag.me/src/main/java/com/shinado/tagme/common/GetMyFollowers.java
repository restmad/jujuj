package com.shinado.tagme.common;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.shinado.tagme.Globals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import framework.inj.entity.Downloadable;

public class GetMyFollowers implements Downloadable{

    public ArrayList<Follower> Follows;

    private String myAccount;

    public GetMyFollowers(String account){
        this.myAccount = account;
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return Globals.URL_TAG_ME + "get_my_follows.php";
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

    @Table(name = "MyFollowers")
    public class Follower extends Model implements Serializable{
        @Column(name = "account")
        public String following_account;
    }
}
