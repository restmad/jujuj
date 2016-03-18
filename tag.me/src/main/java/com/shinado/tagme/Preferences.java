package com.shinado.tagme;

import android.content.Context;

import framework.inj.entity.Downloadable;
import framework.inj.entity.Postable;
import framework.inj.entity.utility.PostButtonListenable;
import provider.PreferenceProvider;

/**
 * Created by shinado on 2016/1/14.
 */
public class Preferences implements Downloadable, Postable, PostButtonListenable{

    public int loginId;

    @Override
    public String onDownLoadUrl(Context context) {
        return PreferenceProvider.URI_GET;
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        return null;
    }

    @Override
    public int getSubmitButtonId() {
        return 0;
    }

    @Override
    public void onPostResponse(Context context, Object obj) {

    }

    @Override
    public String onPostUrl(Context context) {
        return PreferenceProvider.URI_SAVE;
    }

    @Override
    public void onError(Context context, String msg) {

    }

    public void save() {
        mListener.onClick();
    }

    private Listener mListener;

    @Override
    public void listen(Listener l) {
        mListener = l;
    }
}
