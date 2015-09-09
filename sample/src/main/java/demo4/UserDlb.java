package demo4;

import android.content.Context;
import com.shinado.netframe.sample.R;
import java.util.ArrayList;
import framework.inj.ActivityInj;
import framework.inj.ViewValueInj;
import framework.inj.entity.Listable;
import framework.inj.entity.Loadable;
import sample.MyApplication;

@ActivityInj(R.layout.activity_demo4)
public class UserDlb extends Loadable<UserDlb.DldBean>{

    public UserDlb() {
    }

    @ViewValueInj(R.id.user_list)
    public ListUser users(){
        return new ListUser(getEntity().users);
    }

    public UserDlb(DldBean userBeans) {
        super(userBeans);
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return MyApplication.URL + "netframe_get_all_users.php";
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        return null;
    }

    @Override
    public void onError(Context context, String msg) {

    }

    public static class DldBean {

        public ArrayList<UserBean> users;

    }
}
