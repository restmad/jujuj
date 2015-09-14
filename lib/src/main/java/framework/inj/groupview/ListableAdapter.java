package framework.inj.groupview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import framework.core.Jujuj;

/**
 * Created by shinado on 15/9/14.
 */
public class ListableAdapter<T> extends BaseAdapter{

    private Context context;
    private Listable<T> listable;

    public ListableAdapter(Context context, Listable<T> listable){
        this.context = context;
        this.listable = listable;
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
        T item = getItem(i);
        if(view == null){
            view = Jujuj.getInstance().findView(context, viewGroup, item.getClass());
        }
        Jujuj.getInstance().setContent(context, view, item);
        return view;
    }

}
