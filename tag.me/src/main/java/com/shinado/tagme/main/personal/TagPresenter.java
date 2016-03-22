package com.shinado.tagme.main.personal;

import com.shinado.tagme.R;
import com.shinado.tagme.entity.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import framework.inj.GroupViewInj;
import framework.inj.ViewValueInj;
import framework.inj.groupview.AbsList;

@GroupViewInj(R.layout.layout_personal_page_tag)
public class TagPresenter{

    private Tag tag;

    public TagPresenter(Tag tag){
        this.tag = tag;
    }

    @ViewValueInj
    public String tagImg(){
        return tag.img_url;
    }

    public static class Wrapper implements AbsList {

        private List<TagPresenter> tagPresenters;

        public Wrapper(List<Tag> tags){
            tagPresenters = new ArrayList<>();
            for(Tag bean : tags){
                tagPresenters.add(new TagPresenter(bean));
            }
        }

        @Override
        public Collection getList() {
            return tagPresenters;
        }
    }

}
