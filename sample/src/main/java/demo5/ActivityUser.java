package demo5;

import android.content.Context;

import com.shinado.netframe.sample.R;

import java.util.HashMap;

import demo4.UserBean;
import framework.inj.ActivityInj;
import framework.inj.ViewValueInj;
import framework.inj.entity.Following;
import framework.inj.entity.Loadable;
import sample.MyApplication;

@ActivityInj(R.layout.activity_demo2n3)
class ActivityUser extends Loadable<UserBean>{

    public ActivityUser(UserBean userBean) {
        super(userBean);
    }

    @ViewValueInj(R.id.user_name)
    public String userName(){
        return getEntity().userName;
    }

    @ViewValueInj(R.id.married)
    public boolean married(){
        return getEntity().married;
    }

    @ViewValueInj(R.id.user_portrait)
    public String userPortrait(){
        return getEntity().userPortrait;
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return MyApplication.URL + "netframe_get_only_user.php";
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userName", userName());
        return params;
    }

    @Override
    public void onError(Context context, String msg) {

    }
}
