package jujuj.Loadable;

import android.content.Context;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;
import java.util.HashMap;

import framework.inj.ActivityInj;
import framework.inj.ViewValueInj;
import framework.inj.entity.Loadable;
import sample.Constants;

@ActivityInj(R.layout.activity_demo2n3)
public class UserDlb extends Loadable<UserBean>{

    public UserDlb(String userName){
        setEntity(new UserBean(userName));
    }

    @ViewValueInj(R.id.user_name)
    public String userName(){
        return getEntity().userName;
    }

    @ViewValueInj(R.id.user_portrait)
    public String portrait(){
        return getEntity().userPortrait;
    }

    @ViewValueInj(R.id.email)
    public String email(){
        return getEntity().email;
    }

    @ViewValueInj(R.id.married)
    public boolean married(){
        return getEntity().married;
    }

    @ViewValueInj(R.id.number_list)
    public ArrayList<Numbers> numbers(){
        return getEntity().numbers;
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return Constants.URL + "netframe_get_user.php";
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userName", getEntity().userName);
        return params;
    }

    @Override
    public void onError(Context context, String msg) {

    }
}
