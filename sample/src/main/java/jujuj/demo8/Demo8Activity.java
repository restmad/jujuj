package jujuj.demo8;

import android.app.Activity;
import android.os.Bundle;

import java.util.HashSet;

import framework.core.Jujuj;

//demo of Action
public class Demo8Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Jujuj.getInstance().inject(this, new SimpleTagLoader(getMyLikes()));
    }

    private HashSet<Integer> getMyLikes() {
        HashSet<Integer> likes = new HashSet<>();
        likes.add(1);
        likes.add(3);
        likes.add(5);
        return likes;
    }

}
