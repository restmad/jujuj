package com.shinado.tagme.main.home;

import com.shinado.tagme.R;
import com.shinado.tagme.entity.Tag;

import java.util.ArrayList;
import java.util.HashSet;

import framework.inj.ActionInj;
import framework.inj.DependentInj;
import framework.inj.GroupViewInj;
import framework.inj.ViewValueInj;
import framework.inj.groupview.Listable;

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
        return tag.getTitle();
    }

    @ViewValueInj
    public String tagImg(){
        return tag.getImgUrl();
    }

    @ViewValueInj
    public String likeText(){
        return tag.getLikes()+"";
    }

    @ViewValueInj
    public String commentText(){
        return tag.getComments()+"";
    }

    @ViewValueInj
    public boolean followToggle(){
        if (myFollows.contains(tag.getUserAccount())){
            return true;
        }else {
            return false;
        }
    }

    public HomeTagPresenter(Tag tag, HashSet<Integer> myLikes, HashSet<String> myFollows){
        this.tag = tag;
        this.myFollows = myFollows;
        this.myLikes = myLikes;
        userLater = new HomeUserPresenter(tag.getUserAccount());
        likeDrawable = new LikeAction(tag.getId(), myLikes, "");
    }

    public static class Wrapper implements Listable<HomeTagPresenter> {

        private ArrayList<HomeTagPresenter> homeTags;

        public Wrapper(ArrayList<Tag> tags, HashSet<Integer> myLikes, HashSet<String> myFollows){
            homeTags = new ArrayList<>();
            for(Tag bean : tags){
                homeTags.add(new HomeTagPresenter(bean, myLikes, myFollows));
            }
        }

        @Override
        public HomeTagPresenter getItem(int position) {
            return homeTags.get(position);
        }

        @Override
        public int getCount() {
            return homeTags.size();
        }
    }

}
