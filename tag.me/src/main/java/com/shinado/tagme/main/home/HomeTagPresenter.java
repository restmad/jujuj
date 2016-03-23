package com.shinado.tagme.main.home;

import com.shinado.tagme.R;
import com.shinado.tagme.entity.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import framework.inj.ActionInj;
import framework.inj.DependentInj;
import framework.inj.GroupViewInj;
import framework.inj.ViewValueInj;
import framework.inj.groupview.AbsList;

@GroupViewInj(R.layout.layout_home_tag)
public class HomeTagPresenter {

    private Tag tag;
    private HashSet<Integer> myLikes = new HashSet<>();
    private HashSet<String> myFollows = new HashSet<>();

    @DependentInj
    public HomeUserPresenter userLater;

    @ActionInj
    public LikeAction likeDrawable;

    @ViewValueInj
    public String tagTitle(){
        return tag.title;
    }

    @ViewValueInj
    public String tagImg(){
        return tag.img_url;
    }

    @ViewValueInj
    public String likeText(){
        return tag.likes+"";
    }

    @ViewValueInj
    public String commentText(){
        return tag.comments+"";
    }

    @ViewValueInj
    public boolean followToggle(){
        return myFollows.contains(tag.user_account);
    }

    public HomeTagPresenter(Tag tag, HashSet<Integer> myLikes, HashSet<String> myFollows){
        this.tag = tag;
        this.myFollows = myFollows;
        this.myLikes = myLikes;
        userLater = new HomeUserPresenter(tag.user_account);
        likeDrawable = new LikeAction(tag.id, myLikes, "");
    }

    public static class Wrapper implements AbsList{

        private ArrayList<HomeTagPresenter> homeTags;

        public Wrapper(TreeSet<Tag> tags, HashSet<Integer> myLikes, HashSet<String> myFollows){
            homeTags = new ArrayList<>();
            for(Tag bean : tags){
                homeTags.add(new HomeTagPresenter(bean, myLikes, myFollows));
            }
        }

        @Override
        public Collection getList() {
            return homeTags;
        }
    }

}
