package jujuj.multibean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shinado.netframe.sample.R;

import framework.core.Jujuj;
import framework.inj.entity.MutableEntity;

//UserBean is used in multiple activities, using different Presenters
public class Demo5Activity extends Activity {

    private MutableEntity<SimpleUserDlb> userDlb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDlb = new MutableEntity<>(new SimpleUserDlb());
        Jujuj.getInstance().inject(this, userDlb);

        ((ListView) findViewById(R.id.user_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserBean userBean = ((LayoutPresenter) parent.getAdapter().getItem(position)).bean;
                startActivity(new Intent(Demo5Activity.this, UserActivity.class).putExtra("user", userBean));
            }
        });
    }

}
