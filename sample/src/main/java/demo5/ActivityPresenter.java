package demo5;

import com.shinado.netframe.sample.R;

import framework.inj.ActivityInj;
import framework.inj.ViewValueInj;

@ActivityInj(R.layout.activity_demo2n3)
class ActivityPresenter {

    private UserBean bean;

    public ActivityPresenter(UserBean userBean) {
        this.bean = userBean;
    }

    @ViewValueInj(R.id.user_name)
    public String userName(){
        return bean.userName;
    }

    @ViewValueInj(R.id.married)
    public boolean married(){
        return bean.married;
    }

    @ViewValueInj(R.id.user_portrait)
    public String userPortrait(){
        return bean.userPortrait;
    }

    @ViewValueInj(R.id.email)
    public String userMail(){
        return bean.email;
    }


}
