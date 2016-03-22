package com.shinado.tagme.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "TagDep")
public class TagDependency extends Model{

    public TagDependency(int tagId, int hasTagId){
        super();
        this.tagId = tagId;
        this.hasTagId = hasTagId;
    }

    @Column(name = "tagId")
    public int tagId;

    @Column(name = "hasTagId")
    public int hasTagId;

}
