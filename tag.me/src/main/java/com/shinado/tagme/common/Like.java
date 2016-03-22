package com.shinado.tagme.common;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

@Table(name = "MyLikes")
public class Like extends Model implements Serializable {
    @Column(name = "tag_id")
    public int tag_id;

    public Like(){
        super();
    }
}
