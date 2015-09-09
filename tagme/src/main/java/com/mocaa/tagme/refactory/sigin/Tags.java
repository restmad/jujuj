package com.mocaa.tagme.refactory.sigin;

import android.content.Context;

import com.mocaa.tagme.entity.Tag;

import java.util.TreeSet;

import framework.inj.entity.Downloadable;

public class Tags implements Downloadable{

    public TreeSet<Tag> tags;

    @Override
    public String onDownLoadUrl(Context context) {
        return null;
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

    }
}
