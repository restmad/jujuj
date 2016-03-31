package jujuj.demo5;

import android.app.Activity;
import android.os.Bundle;

import framework.core.Jujuj;

/**
 * Created by shinado on 15/9/14.
 */
public class UserActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserBean user = (UserBean) getIntent().getSerializableExtra("user");
        Jujuj.getInstance().inject(this, new ActivityPresenter(user));
    }
}
