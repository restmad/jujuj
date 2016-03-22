package framework.inj.groupview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Collection;

import framework.core.Jujuj;

public class ListableAdapter extends BaseAdapter{

    private Context context;
    private Collection<?> items;
    private String packageName;

    public ListableAdapter(Context context, AbsList list, String packageName){
        this.context = context;
        this.items = list.getList();
        this.packageName = packageName;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.toArray()[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        long startTime = System.currentTimeMillis();
        Object item = getItem(i);
        if(view == null){
            view = Jujuj.getInstance().findViewForGroup(context, viewGroup, item.getClass());
        }
        Jujuj.getInstance().setContent(context, view, item, packageName);
        Log.d("JujujTime", "getView:" + (System.currentTimeMillis() - startTime));
        return view;
    }

}
