package demo5;

import android.app.Activity;
import android.os.Bundle;

import framework.core.Jujuj;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.utility.Notifiable;

/**
 * Created by shinado on 15/9/14.
 */
public class Demo5Activity extends Activity implements Notifiable{

    private MutableEntity<SimpleUserDlb> userDlb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDlb = new MutableEntity<>(new SimpleUserDlb(), this);
        Jujuj.getInstance().inject(this, userDlb);
    }

    @Override
    public void onDownloadResponse() {

    }
}
