package demo6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shinado.netframe.sample.R;

import java.util.ArrayList;

import framework.core.Jujuj;
import framework.inj.GroupViewInj;
import framework.inj.ViewDisplay;
import framework.inj.ViewValueInj;
import framework.inj.entity.Loadable;
import framework.inj.entity.MutableEntity;
import framework.inj.groupview.Adaptable;
import framework.inj.groupview.Listable;

public class LayoutPresenter implements Adaptable {

    private ArrayList<PostBean> posts;

    public LayoutPresenter(ArrayList<PostBean> posts){
        this.posts = posts;
    }

    @Override
    public BaseAdapter getAdapter(final Context context) {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return posts == null ? 0 : posts.size();
            }

            @Override
            public PostBean getItem(int position) {
                return posts.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.layout_demo6_item, parent, false);
                    holder = new ViewHolder();
                    holder.postContent = (TextView) convertView.findViewById(R.id.post_content);
                    holder.postImg = (ImageView) convertView.findViewById(R.id.post_image);
                    convertView.setTag(holder);
                }else{
                    holder = (ViewHolder) convertView.getTag();
                }

                PostBean bean = getItem(position);
                holder.postContent.setText(bean.content);
                ImageLoader.getInstance().displayImage(bean.image, holder.postImg);
                Jujuj.getInstance().inject(context, convertView, new SingleUserDlb(bean.userId));

                return convertView;
            }

            class ViewHolder{
                TextView postContent;
                ImageView postImg;
            }
        };
    }

}
