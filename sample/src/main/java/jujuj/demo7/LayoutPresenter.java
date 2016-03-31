package jujuj.demo7;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import framework.inj.GroupViewInj;
import framework.inj.ViewValueInj;
import framework.inj.groupview.AbsList;

@GroupViewInj(R.layout.layout_demo5_user)
public class LayoutPresenter {

    public LayoutPresenter(UserBean bean){
        this.bean = bean;
    }

    private UserBean bean;

    @ViewValueInj(R.id.user_name)
    public String userName(){
        return bean.userName;
    }

    @ViewValueInj(R.id.user_portrait)
    public String userPortrait(){
        return bean.userPortrait;
    }


    public static class Wrapper implements AbsList{

        private ArrayList<LayoutPresenter> layoutUsers;

        public Wrapper(TreeSet<UserBean> users){
            layoutUsers = new ArrayList<>();
            for(UserBean bean : users){
                layoutUsers.add(new LayoutPresenter(bean));
            }
        }

        @Override
        public Collection getList() {
            return layoutUsers;
        }
    }
}
