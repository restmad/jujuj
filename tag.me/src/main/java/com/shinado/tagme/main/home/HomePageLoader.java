package com.shinado.tagme.main.home;

import android.content.Context;
import android.widget.Toast;

import com.shinado.tagme.Globals;
import com.shinado.tagme.entity.Tag;

import java.util.ArrayList;

import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.utility.Transformable;

public class HomePageLoader implements Downloadable, Transformable{

    @ViewInj
    public ArrayList<Tag> tags;

    @Override
    public String onDownLoadUrl(Context context) {
        return Globals.URL_MAIN + "get_tags";
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        return null;
    }

    @Override
    public void onError(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public Object fromServer(String fieldName, Object value) {
        if(value == tags){
            return new HomeTagPresenter.Wrapper(tags);
        }else{
            return value;
        }
    }

    @Override
    public Object toServer(String fieldName, Object value) {
        return value;
    }
}
