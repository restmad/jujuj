package plain.demo2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shinado.netframe.sample.R;

import java.util.List;

/**
 * Created by shinado on 2016/1/20.
 */
public class UserAdapter extends BaseAdapter{

    private Context mContext;
    private List<Number> mNumbers;

    public UserAdapter(Context context, List<Number> numbers){
        this.mContext = context;
        this.mNumbers = numbers;
    }

    @Override
    public int getCount() {
        return mNumbers == null ? 0 : mNumbers.size();
    }

    @Override
    public Number getItem(int position) {
        return mNumbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_demo2n3_number, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.contact_number);
        Number number = getItem(position);
        textView.setText(number.number);
        return convertView;
    }
}
