package com.shinado.tagme.main.home;

import android.content.Context;

import com.shinado.tagme.BaseResult;
import com.shinado.tagme.Globals;
import com.shinado.tagme.R;

import java.util.HashMap;
import java.util.HashSet;

import framework.inj.entity.Action;

public class LikeAction implements Action<BaseResult> {

    private HashSet<Integer> myLikes;
    private int tagId;
    private String myAccount;

    public LikeAction(int tagId, HashSet<Integer> myLikes, String myAccount){
        this.tagId = tagId;
        this.myLikes = myLikes;
        this.myAccount = myAccount;
    }

    @Override
    public Integer getValue() {
        if (myLikes.contains(tagId)){
            return R.drawable.ic_like;
        }else{
            return R.drawable.ic_not_like;
        }
    }

    @Override
    public void onPostResponse(Context context, BaseResult obj) {
        if (obj.resultCode > 0){
            myLikes.add(tagId);
        }else{
            myLikes.remove(tagId);
        }
    }

    @Override
    public String onPostUrl(Context context) {
        return Globals.URL_TAG_ME + "like.php";
    }

    @Override
    public void onError(Context context, String msg) {

    }

    @Override
    public Object onDownloadParams() {
        HashMap<String, String> params = new HashMap<>();

        params.put("user_account", myAccount);
        params.put("tag_id", ""+tagId);

        return params;
    }
}
