package jujuj.post;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.shinado.netframe.sample.R;

import jujuj.downloadable.Demo2Activity;
import framework.inj.ActivityInj;
import framework.inj.ViewInj;
import framework.inj.entity.Postable;
import sample.Constants;

@ActivityInj(R.layout.activity_demo1)
public class LoginRequest implements Postable<LoginRequest.Result> {

    @ViewInj(R.id.login_account) public String account;

    @ViewInj(R.id.login_pwd) public String pwd;

    private PostActivity mActivity;

    public LoginRequest(PostActivity activity){
        this.mActivity = activity;
    }

    @Override public int getSubmitButtonId() {return R.id.login_submit;}

    @Override public void onPostResponse(Context context, LoginRequest.Result result) {
        mActivity.dismissDialog();
        if(result.id > 0){
            context.startActivity(new Intent(context, Demo2Activity.class));
        }else{
            Toast.makeText(context, "login failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override public String onPostUrl(Context context) {
        mActivity.showDialog();
        return Constants.URL + "netframe_sign_in.php";
    }

    @Override public void onError(Context context, String msg) {
        mActivity.dismissDialog();
    }

    public class Result{
        public int id;
    }
}
