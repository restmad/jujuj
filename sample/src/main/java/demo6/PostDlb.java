package demo6;

import android.content.Context;

import com.shinado.netframe.sample.R;

import demo6.entity.Posts;
import framework.inj.ActivityInj;
import framework.inj.ViewValueInj;
import framework.inj.entity.Loadable;
import sample.Constants;

@ActivityInj(R.layout.activity_demo6)
public class PostDlb extends Loadable<Posts> {

    @ViewValueInj(R.id.post_list)
    public PostPresenter.Wrapper posts(){
        return new PostPresenter.Wrapper(getEntity().posts);
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

}
