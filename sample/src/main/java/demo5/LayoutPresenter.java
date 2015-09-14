package demo5;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;

import framework.inj.GroupViewInj;
import framework.inj.ViewValueInj;
import framework.inj.groupview.Listable;

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

    @ViewValueInj(R.id.user_email)
    public String userMail(){
        return bean.email;
    }

    @ViewValueInj(R.id.married)
    public boolean married(){
        return bean.married;
    }

    public static class Wrapper implements Listable<LayoutPresenter>{

        public Wrapper(ArrayList<UserBean> users){
            layoutUsers = new ArrayList<>();
            for(UserBean bean : users){
                layoutUsers.add(new LayoutPresenter(bean));
            }
        }

        private ArrayList<LayoutPresenter> layoutUsers;

        @Override
        public LayoutPresenter getItem(int position) {
            return layoutUsers.get(position);
        }

        @Override
        public int getCount() {
            return layoutUsers.size();
        }
    }
}
