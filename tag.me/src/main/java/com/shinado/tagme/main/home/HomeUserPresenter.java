package com.shinado.tagme.main.home;

import com.shinado.tagme.user.BaseUserLoader;

import framework.inj.ViewValueInj;

public class HomeUserPresenter extends BaseUserLoader{

    public HomeUserPresenter(String account) {
        super(account);
    }

    @ViewValueInj
    public String userName(){
        return getEntity().getUserName();
    }

    @ViewValueInj
    public String userPortrait(){
        return getEntity().getPortrait();
    }

}
