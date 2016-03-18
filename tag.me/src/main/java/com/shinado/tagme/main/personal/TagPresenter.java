package com.shinado.tagme.main.personal;

import com.shinado.tagme.R;
import com.shinado.tagme.entity.Tag;

import java.util.ArrayList;
import java.util.List;

import framework.inj.GroupViewInj;
import framework.inj.ViewValueInj;
import framework.inj.groupview.Listable;

@GroupViewInj(R.layout.layout_personal_page_tag)
public class TagPresenter{

    private Tag tag;

    public TagPresenter(Tag tag){
        this.tag = tag;
    }

    @ViewValueInj
    public String tagImg(){
        return tag.getImgUrl();
    }

    public static class Wrapper implements Listable<TagPresenter> {

        private List<TagPresenter> tagPresenters;

        public Wrapper(List<Tag> tags){
            tagPresenters = new ArrayList<>();
            for(Tag bean : tags){
                tagPresenters.add(new TagPresenter(bean));
            }
        }

        @Override
        public TagPresenter getItem(int position) {
            return tagPresenters.get(position);
        }

        @Override
        public int getCount() {
            return tagPresenters.size();
        }
    }

}
