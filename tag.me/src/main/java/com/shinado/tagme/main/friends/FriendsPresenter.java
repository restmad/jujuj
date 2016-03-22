package com.shinado.tagme.main.friends;

import com.shinado.tagme.R;
import com.shinado.tagme.user.User;

import java.util.ArrayList;
import java.util.Collection;

import framework.inj.GroupViewInj;
import framework.inj.ViewValueInj;
import framework.inj.groupview.AbsList;

@GroupViewInj(R.layout.layout_friends)
public class FriendsPresenter {

    private User friend;

    @ViewValueInj
    public String userPortrait(){
        return friend.getPortrait();
    }

    @ViewValueInj
    public String userName(){
        return friend.getUserName();
    }

    @ViewValueInj
    public String userId(){
        return friend.getUniqueId();
    }

    @ViewValueInj
    public boolean followToggle(){
        return false;
    }

    public FriendsPresenter(User friend){
        this.friend = friend;
    }

    public static class Wrapper implements AbsList{

        private ArrayList<FriendsPresenter> friends;

        public Wrapper(ArrayList<User> users){
            this.friends = new ArrayList<>();
            for(User bean : users){
                this.friends.add(new FriendsPresenter(bean));
            }
        }

        @Override
        public Collection getList() {
            return friends;
        }
    }

}
