package com.shinado.tagme.common;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

@Table(name = "MyFollowers")
public class Follower extends Model implements Serializable {
    @Column(name = "account")
    public String following_account;

    public Follower() {
        super();
    }
}
