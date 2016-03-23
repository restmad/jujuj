package demo6.entity;

import java.util.ArrayList;
import java.util.Collection;

import demo6.entity.PostBean;
import framework.inj.entity.Listable;

public class Posts implements Listable{

    public ArrayList<PostBean> posts;

    @Override
    public Collection getList() {
        return posts;
    }
}
