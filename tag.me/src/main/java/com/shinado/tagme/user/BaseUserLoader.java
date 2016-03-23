package com.shinado.tagme.user;

import android.content.Context;

import com.shinado.tagme.Globals;
import com.shinado.tagme.URLs;

import java.util.HashMap;

import framework.inj.entity.Loadable;

public class BaseUserLoader extends Loadable<User>{

    public BaseUserLoader(String account){
        setEntity(new User(account));
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return Globals.URL_TAG_ME + URLs.GET_USERS;
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_account", getEntity().getAccount());
        return params;
    }

    @Override
    public void onError(Context context, String msg) {

    }
}
