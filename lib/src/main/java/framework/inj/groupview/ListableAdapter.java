package framework.inj.groupview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import framework.core.Jujuj;

public class ListableAdapter<T> extends BaseAdapter{

    private Context context;
    private Listable<T> listable;
    private String packageName;

    public ListableAdapter(Context context, Listable<T> listable, String packageName){
        this.context = context;
        this.listable = listable;
        this.packageName = packageName;
    }

    @Override
    public int getCount() {
        return listable.getCount();
    }

    @Override
    public T getItem(int i) {
        return listable.getItem(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        long startTime = System.currentTimeMillis();
        T item = getItem(i);
        if(view == null){
            view = Jujuj.getInstance().findViewForGroup(context, viewGroup, item.getClass());
        }
        Jujuj.getInstance().setContent(context, view, item, packageName);
        Log.d("JujujTime", "getView:" + (System.currentTimeMillis() - startTime));
        return view;
    }

}
