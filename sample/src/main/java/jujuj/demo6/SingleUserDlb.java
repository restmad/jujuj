package jujuj.demo6;

import android.content.Context;

import com.shinado.netframe.sample.R;

import java.util.HashMap;

import jujuj.demo6.entity.UserBean;
import framework.inj.ViewValueInj;
import framework.inj.entity.Loadable;
import sample.Constants;

public class SingleUserDlb extends Loadable<UserBean>{

    public SingleUserDlb(int userId){
        setEntity(new UserBean(userId));
    }

    @ViewValueInj(R.id.user_name)
    public String name(){
        return getEntity().userName;
    }

    @ViewValueInj(R.id.user_portrait)
    public String portrait(){
        return getEntity().userPortrait;
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return Constants.URL + "netframe_get_user_by_id.php";
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", getEntity().userId+"");
        return params;
    }

    @Override
    public void onError(Context context, String msg) {

    }
}
