package jujuj.listable;

import java.util.Collection;
import java.util.TreeSet;

import framework.inj.entity.Listable;

public class Users implements Listable{

    public TreeSet<UserBean> users;

    @Override
    public Collection getList() {
        return users;
    }
}
