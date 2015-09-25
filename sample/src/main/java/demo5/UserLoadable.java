package demo5;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;

import framework.inj.ActivityInj;
import framework.inj.OnItemClick;
import framework.inj.ViewInj;
import framework.inj.ViewValueInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Loadable;
import framework.inj.entity.utility.Transformable;
import sample.MyApplication;

@ActivityInj(R.layout.activity_demo5)
public class UserLoadable extends Loadable<ArrayList<UserBean>> implements Transformable{

    @ViewValueInj(R.id.user_list)
    public ArrayList<UserBean> users(){
        return getEntity();
    }

    @OnItemClick(R.id.user_list)
    public void onItemClick(Context context, View v, int i){
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra("user", getEntity().get(i));
        context.startActivity(intent);
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

    @Override
    public Object fromServer(String fieldName, Object value) {
        ArrayList<UserBean> users = getEntity();
        if(value == users){
            return new LayoutPresenter.Wrapper(users);
        }else{
            return value;
        }
    }

    @Override
    public Object toServer(String fieldName, Object value) {
        return value;
    }
}
