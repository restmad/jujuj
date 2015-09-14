package demo5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shinado.netframe.sample.R;

import framework.core.Jujuj;
import framework.inj.entity.MutableEntity;

/**
 * Created by shinado on 15/9/14.
 */
public class Demo5Activity extends Activity{

    private MutableEntity<UserDlb> userDlb = new MutableEntity<>(new UserDlb());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Jujuj.getInstance().inject(this, userDlb);

        ((ListView)findViewById(R.id.user_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserBean bean = userDlb.getEntity().users.get(i);
                Intent intent = new Intent(Demo5Activity.this, UserActivity.class);
                intent.putExtra("user", bean);
                startActivity(intent);
            }
        });

    }
}
