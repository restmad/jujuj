package jujuj.demo6;

import android.app.Activity;
import android.os.Bundle;
import framework.core.Jujuj;

//sample of DependentInj
public class Demo6Activity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Jujuj.getInstance().inject(this, new PostDlb());
    }
}
