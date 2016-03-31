package jujuj.demo7;

import android.app.Activity;
import android.os.Bundle;

import framework.core.Jujuj;
import framework.inj.entity.MutableEntity;

//sample of Listable, loading users from different providers
public class Demo7Activity extends Activity{

    private MutableEntity<SimpleUserDlb> userDlb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDlb = new MutableEntity<>(new SimpleUserDlb());
        Jujuj.getInstance().inject(this, userDlb);
    }

}
