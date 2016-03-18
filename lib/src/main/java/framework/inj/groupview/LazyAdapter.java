package framework.inj.groupview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Collection;

import framework.core.Jujuj;

public class LazyAdapter extends BaseAdapter{

    private Context mContext;
    private Collection<Object> mData;
    private String mPackageName;

    public LazyAdapter(Context context, Collection<Object> data, String packageName) {
    	this.mContext = context;
        mData = data;
        mPackageName = packageName;
    }

    public void setData(Collection<Object> items){
    	mData = items;
    	notifyDataSetChanged();
    }
    
    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
		Log.d("HttpRequest", "adapter count:"+mData.size());
        return mData.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return mData.toArray()[position];
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent) {
        View v = null;
        if (convertView == null) {
        	if(mData !=null && mData.size() > 0){
                v = Jujuj.getInstance().findViewForGroup(mContext, parent, mData.toArray()[0].getClass());
        	}
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }
    
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    private void bindView(int position, View view) {
        final Object dataSet =  getItem(position);
        if (dataSet == null) {
            return;
        }
		Log.d("HttpRequest", "adapter get item:" + dataSet.toString());

        //TODO use tag
        Jujuj.getInstance().setContent(mContext, view, dataSet, mPackageName);
    }

    /**
     * This class can be used by external clients of SimpleAdapter to bind
     * values to views.
     *
     * You should use this class to bind values to views that are not
     * directly supported by SimpleAdapter or to change the way binding
     * occurs for views supported by SimpleAdapter.
     *
     * @see SimpleAdapter#setViewImage(ImageView, int)
     * @see SimpleAdapter#setViewImage(ImageView, String)
     * @see SimpleAdapter#setViewText(TextView, String)
     */
    public interface ViewBinder {
        /**
         * Binds the specified data to the specified view.
         *
         * When binding is handled by this ViewBinder, this method must return true.
         * If this method returns false, SimpleAdapter will attempts to handle
         * the binding on its own.
         *
         * @param view the view to bind the data to
         * @param data the data to bind to the view
         * @param textRepresentation a safe String representation of the supplied data:
         *        it is either the result of data.toString() or an empty String but it
         *        is never null
         *
         * @return true if the data was bound to the view, false otherwise
         */
        boolean setViewValue(View view, Object data, String textRepresentation);
    }

}