package demo6;

import android.content.Context;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;

import framework.inj.ActivityInj;
import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.utility.Transformable;
import sample.Constants;

@ActivityInj(R.layout.activity_demo6)
public class PostDlb implements Downloadable, Transformable{

    @ViewInj(R.id.post_list)
    public ArrayList<PostBean> posts;

    @Override
    public String onDownLoadUrl(Context context) {
        return Constants.URL + "netframe_get_all_posts.php";
    }

    @Override
    public void onDownLoadResponse(Context context) {
    }

    @Override
    public Object onDownloadParams() {
        return null;
    }

    @Override
    public void onError(Context context, String msg) {

    }

    @Override
    public Object fromServer(String fieldName, Object value) {
        if(value == posts){
            return new LayoutPresenter(posts);
        }else{
            return value;
        }
    }

    @Override
    public Object toServer(String fieldName, Object value) {
        return value;
    }
}
