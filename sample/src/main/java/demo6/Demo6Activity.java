package demo6;

import android.app.Activity;
import android.os.Bundle;

import framework.core.Jujuj;
import framework.inj.entity.MutableEntity;

/**
 * Created by Administrator on 2015/9/17.
 */
public class Demo6Activity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Jujuj.getInstance().inject(this, new MutableEntity<PostDlb>(new PostDlb()));
    }
}
