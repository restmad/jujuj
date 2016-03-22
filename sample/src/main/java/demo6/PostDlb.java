package demo6;

import android.content.Context;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;

import demo6.entity.PostBean;
import demo6.entity.Posts;
import framework.inj.ActivityInj;
import framework.inj.ViewValueInj;
import framework.inj.entity.Loadable;
import framework.inj.entity.utility.Transformable;
import sample.Constants;

@ActivityInj(R.layout.activity_demo6)
public class PostDlb extends Loadable<Posts> implements Transformable{

    @ViewValueInj(R.id.post_list)
    public ArrayList<PostBean> posts(){
        return getEntity().posts;
    }

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
        if(fieldName.endsWith("posts")){
            return new PostPresenter.Wrapper(posts());
        }else{
            return value;
        }
    }

    @Override
    public Object toServer(String fieldName, Object value) {
        return value;
    }
}
