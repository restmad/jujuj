package demo4;

import android.content.Context;

import com.shinado.netframe.sample.R;

import framework.inj.ActivityInj;
import framework.inj.ViewValueInj;

@ActivityInj(R.layout.activity_demo4)
public class LayoutUser {

    private UserBean bean;

    @ViewValueInj(R.id.user_name)
    public String userName(){
        return bean.userName;
    }
}
