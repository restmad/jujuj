package demo2;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shinado.netframe.sample.R;

import org.json.JSONObject;

import java.util.HashMap;

import framework.provider.Listener;
import provider.volley.NetworkRequest;
import provider.volley.VolleyProvider;
import sample.MyApplication;

/**
 * Created by shinado on 15/8/31.
 */
public class Demo0Activity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2n3);

        String userName = "Dan";
        //query in database
        UserBean entity = new Select().from(UserBean.class).where("userName = ?", userName).executeSingle();
        if(entity != null){
            entity.numbers = entity.numbers();
        }

        if(entity == null){
            //load from server
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userName", userName);
            new VolleyProvider().handleData(MyApplication.URL + "netframe_get_user.php", params, UserBean.class,
                    new Listener.Response<UserBean>(){

                        @Override
                        public void onResponse(UserBean bean) {
                            if (bean != null) {
                                bean.save();
                                setViewWithData(bean);
                            }
                        }
                    },
            new Listener.Error(){

                @Override
                public void onError(String msg) {

                }
            });
        }else{
            setViewWithData(entity);
        }
    }

    private void setViewWithData(final UserBean bean){
        //find view and set value
        ImageView portrait = (ImageView) findViewById(R.id.user_portrait);
        ImageLoader.getInstance().displayImage(bean.userPortrait, portrait);
        TextView userName = (TextView) findViewById(R.id.user_name);
        userName.setText(bean.userName);
        CheckBox married = (CheckBox) findViewById(R.id.married);
        married.setChecked(bean.married);

        //find view for listView
        ListView numbers = (ListView) findViewById(R.id.number_list);
        numbers.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return bean.numbers.size();
            }

            @Override
            public String getItem(int i) {
                return bean.numbers.get(i).number;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                ViewHolder holder = null;
                if(view == null){
                    view = LayoutInflater.from(Demo0Activity.this).inflate(R.layout.layout_demo2n3_number, viewGroup, false);
                    holder = new ViewHolder();
                    holder.number = (TextView) view.findViewById(R.id.contact_number);
                    view.setTag(holder);
                }
                holder = (ViewHolder) view.getTag();
                holder.number.setText(getItem(i));

                return view;
            }

            class ViewHolder{
                TextView number;
            }
        });
    }


}
