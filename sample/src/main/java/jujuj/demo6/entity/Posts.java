package jujuj.demo6.entity;

import java.util.ArrayList;
import java.util.Collection;

import framework.inj.entity.Listable;

public class Posts implements Listable{

    public ArrayList<PostBean> posts;

    @Override
    public Collection getList() {
        return posts;
    }
}
