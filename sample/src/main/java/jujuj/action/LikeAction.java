package jujuj.action;

import android.content.Context;

import java.util.HashMap;
import java.util.HashSet;

import framework.inj.entity.Action;
import sample.Constants;

public class LikeAction implements Action<LikeAction.Result> {

    private HashSet<Integer> myLikes;
    private int tagId ;

    public LikeAction(HashSet<Integer> myLikes, int tagId){
        this.myLikes = myLikes;
        this.tagId = tagId;
    }

    @Override
    public Object getValue() {
        return myLikes.contains(tagId);
    }

    @Override
    public Object onDownloadParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", tagId+"");
        return params;
    }

    @Override
    public void onPostResponse(Context context, Result obj) {
        if (obj.resultCode == 1){
            myLikes.add(tagId);
        }else if (obj.resultCode == 0){
            myLikes.remove(tagId);
        }
    }

    @Override
    public String onPostUrl(Context context) {
        return Constants.URL + "netframe_like.php";
    }

    @Override
    public void onError(Context context, String msg) {

    }

    public class Result{
        public int resultCode;
        public String msg;
    }
}
