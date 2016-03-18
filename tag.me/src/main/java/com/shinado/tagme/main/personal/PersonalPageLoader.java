package com.shinado.tagme.main.personal;

import android.content.Context;
import android.util.Log;

import com.shinado.tagme.Globals;
import com.shinado.tagme.R;
import com.shinado.tagme.entity.Tag;
import com.shinado.tagme.user.User;

import java.util.List;

import framework.inj.ViewInj;
import framework.inj.ViewValueInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.utility.Transformable;

public class PersonalPageLoader implements Downloadable, Transformable{

    @ViewInj
    public List<Tag> tags;

    private User user;

    @ViewValueInj
    public String userPortrait(){
        return user.getPortrait();
    }

    @ViewValueInj
    public String userName(){
        return user.getUserName();
    }

    @ViewValueInj
    public int genderImg(){
        return user.getGender() == User.MALE ? R.drawable.ic_male : R.drawable.ic_female;
    }

    @ViewValueInj
    public String userLiving(){
        return "Living in " + user.getPlace();
    }

    @ViewValueInj
    public String tagCount(){
        return user.getTags() + "";
    }

    @ViewValueInj
    public String followers(){
        return user.getFollower() + "";
    }

    @ViewValueInj
    public String follows(){
        return user.getFollowing() + "";
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return Globals.URL_MAIN + "get_personal_tags";
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
        Log.e("error", msg);
    }

    @Override
    public Object fromServer(String fieldName, Object value) {
        if (value == tags){
            return new TagPresenter.Wrapper(tags);
        }
        return value;
    }

    @Override
    public Object toServer(String fieldName, Object value) {
        return value;
    }
}
