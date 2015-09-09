package demo4;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

import framework.inj.groupview.Adaptable;

public class ListUser implements Adaptable{

    private ArrayList<UserBean> users;

    public ListUser(ArrayList<UserBean> users){
        this.users = users;
    }

    @Override
    public BaseAdapter getAdapter(Context context) {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return users.size();
            }

            @Override
            public UserBean getItem(int i) {
                return users.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                return view;
            }
        };
    }

}
