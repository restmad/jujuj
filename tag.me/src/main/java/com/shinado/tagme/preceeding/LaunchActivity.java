package com.shinado.tagme.preceeding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.activeandroid.query.Select;
import com.shinado.tagme.common.GetMyFollowers;
import com.shinado.tagme.common.GetMyLikes;
import com.shinado.tagme.common.UserPref;
import com.shinado.tagme.login.LoginActivity;
import com.shinado.tagme.main.MainActivity;

import java.util.HashSet;
import java.util.List;

public class LaunchActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(new UserPref(this).isFirstTime()){
//            startActivity(new Intent(this, GuideActivity.class));
        }else{
            if(UserPref.signedIn(this)){
                startMainActivity();
            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
        finish();
    }

    private void startMainActivity(){

        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.EXTRA_MY_LIKES, getMyLikes());
        bundle.putSerializable(MainActivity.EXTRA_MY_FOLLOWERS, getMyFollows());
        intent.putExtras(bundle);

        this.startActivity(intent);
    }

    private HashSet<Integer> getMyLikes(){
        List<GetMyLikes.Like> likes = new Select().all().from(GetMyLikes.Like.class).execute();

        HashSet<Integer> myLikes = new HashSet<>();
        for (GetMyLikes.Like l : likes){
            myLikes.add(l.tag_id);
        }
        return myLikes;
    }

    private HashSet<String> getMyFollows(){
        List<GetMyFollowers.Follower> followers = new Select().all().from(GetMyFollowers.Follower.class).execute();


        HashSet<String> myFollows = new HashSet<>();
        for (GetMyFollowers.Follower f : followers){
            myFollows.add(f.following_account);
        }

        return myFollows;
    }
}
