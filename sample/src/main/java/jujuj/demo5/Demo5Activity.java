package jujuj.demo5;

import android.app.Activity;
import android.os.Bundle;

import framework.core.Jujuj;
import framework.inj.entity.MutableEntity;

//sample of AbsList
//UserBean is used in multiple activities, using different Presenters
public class Demo5Activity extends Activity {

    private MutableEntity<SimpleUserDlb> userDlb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDlb = new MutableEntity<>(new SimpleUserDlb());
        Jujuj.getInstance().inject(this, userDlb);
    }

}
