package com.shinado.tagme.entity;

import java.util.ArrayList;
import java.util.Collection;

import framework.inj.entity.Listable;

public class Tags implements Listable{

    public ArrayList<Tag> tags = new ArrayList<>();

    @Override
    public Collection getList() {
        return tags;
    }
}
