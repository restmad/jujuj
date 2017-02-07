package jujuj.proxy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.shinado.netframe.sample.R;

import framework.inj.ActivityInj;
import framework.inj.ViewInj;
import framework.inj.entity.Postable;
import jujuj.downloadable.Demo2Activity;
import sample.Constants;

@ActivityInj(R.layout.activity_demo1)
public class LoginRequest implements Postable<LoginRequest.Result>{

    @ViewInj(R.id.login_account) public String account;

    @ViewInj(R.id.login_pwd) public String pwd;

    private ProxyActivity mActivity;

    public LoginRequest(ProxyActivity activity){
        this.mActivity = activity;
    }

    @Override public int getSubmitButtonId() {return R.id.login_submit;}

    @Override public void onPostResponse(Context context, LoginRequest.Result result) {
        if(result.id > 0){
            context.startActivity(new Intent(context, Demo2Activity.class));
            ((Activity)context).finish();
        }else{
            Toast.makeText(context, "login failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override public String onPostUrl(Context context) {
        mActivity.showDialog();
        return Constants.URL + "netframe_sign_in.php";
    }

    @Override public void onError(Context context, String msg) {
    }

    public class Result{
        public int id;
    }
}
