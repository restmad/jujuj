package jujuj.action;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import framework.inj.ActionInj;
import framework.inj.GroupViewInj;
import framework.inj.ViewValueInj;
import framework.inj.groupview.AbsList;

@GroupViewInj(R.layout.layout_demo8_item)
public class LayoutPresenter {

    private SimpleTags.Tag tag;

    public LayoutPresenter(HashSet<Integer> myLikes, SimpleTags.Tag tag){
        this.tag = tag;
        like = new LikeAction(myLikes, tag.id);
    }

    @ActionInj
    public LikeAction like;

    @ViewValueInj
    public String content(){
        return tag.content;
    }

    public static class Wrapper implements AbsList {

        private ArrayList<LayoutPresenter> layoutUsers;

        public Wrapper(HashSet<Integer> myLikes, List<SimpleTags.Tag> tags){
            layoutUsers = new ArrayList<>();
            for(SimpleTags.Tag bean : tags){
                layoutUsers.add(new LayoutPresenter(myLikes, bean));
            }
        }

        @Override
        public Collection getList() {
            return layoutUsers;
        }
    }
}
