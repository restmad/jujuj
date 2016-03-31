package jujuj.demo7;

import android.content.Context;

import com.shinado.netframe.sample.R;

import java.util.HashMap;

import framework.inj.ActivityInj;
import framework.inj.ViewValueInj;
import framework.inj.entity.Loadable;
import sample.Constants;

@ActivityInj(R.layout.activity_demo5)
public class SimpleUserDlb extends Loadable<Users> {

    public static final String URL_SUFFIX = "netframe_get_user_list.php";

    @ViewValueInj(R.id.user_list)
    public LayoutPresenter.Wrapper users(){
        return new LayoutPresenter.Wrapper(getEntity().users);
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return Constants.URL + URL_SUFFIX;
    }

    @Override
    public void onDownLoadResponse(Context context) {
    }

    @Override
    public Object onDownloadParams() {
        //
        HashMap<String, String> params = new HashMap<>();
        Users users = getEntity();
        if (users != null && users.users != null && users.users.size() > 0){
            params.put("lastId", users.users.last()+"");
        }
        return params;
    }

    @Override
    public void onError(Context context, String msg) {
    }

}
