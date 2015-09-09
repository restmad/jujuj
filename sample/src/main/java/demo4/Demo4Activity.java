package demo4;

import android.app.Activity;
import android.os.Bundle;

import framework.core.Jujuj;

public class Demo4Activity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Jujuj.getInstance().inject(this, new UserDlb());
    }
}
