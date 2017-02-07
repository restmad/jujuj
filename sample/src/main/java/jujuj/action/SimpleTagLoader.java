package jujuj.action;

import android.content.Context;

import com.shinado.netframe.sample.R;

import java.util.HashSet;

import framework.inj.ActivityInj;
import framework.inj.ViewValueInj;
import framework.inj.entity.Loadable;
import sample.Constants;

@ActivityInj(R.layout.activity_demo8)
public class SimpleTagLoader extends Loadable<SimpleTags> {

    private HashSet<Integer> myLikes;

    public SimpleTagLoader(HashSet<Integer> myLikes){
        this.myLikes = myLikes;
        setEntity(new SimpleTags());
    }

    @ViewValueInj(R.id.tag_list)
    public LayoutPresenter.Wrapper tags(){
        return new LayoutPresenter.Wrapper(myLikes, getEntity().tags);
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return Constants.URL + "netframe_get_simple_tags.php";
    }

    @Override
    public Object onDownloadParams() {
        return null;
    }

    @Override
    public void onError(Context context, String msg) {

    }

    @Override
    public void onDownLoadResponse(Context context) {

    }
}
