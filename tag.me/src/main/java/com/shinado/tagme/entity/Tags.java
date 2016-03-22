package com.shinado.tagme.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import framework.inj.entity.Listable;

public class Tags implements Listable{

    public List<Tag> tags = new ArrayList<>();

    @Override
    public Collection getList() {
        return tags;
    }
}
