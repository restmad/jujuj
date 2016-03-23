package com.shinado.tagme.entity;

import java.util.Collection;
import java.util.TreeSet;

import framework.inj.entity.Listable;

public class Tags implements Listable{

    public TreeSet<Tag> tags = new TreeSet<>();

    @Override
    public Collection getList() {
        return tags;
    }
}
