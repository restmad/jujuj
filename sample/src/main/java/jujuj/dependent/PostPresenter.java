package jujuj.dependent;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;
import java.util.Collection;

import jujuj.dependent.entity.PostBean;
import framework.inj.DependentInj;
import framework.inj.GroupViewInj;
import framework.inj.ViewValueInj;
import framework.inj.groupview.AbsList;

@GroupViewInj(R.layout.layout_demo6_item)
public class PostPresenter {

    private PostBean post;

    @ViewValueInj(R.id.post_image)
    public String postImage(){
        return post.image;
    }

    @ViewValueInj(R.id.post_content)
    public String postContent(){
        return post.content;
    }

    @DependentInj
    public SingleUserDlb user;

    public PostPresenter(PostBean post){
        this.post = post;
        this.user = new SingleUserDlb(post.userId);
    }

    public static class Wrapper implements AbsList{

        private ArrayList<PostPresenter> list;

        public Wrapper(ArrayList<PostBean> posts){
            list = new ArrayList<>();
            for(PostBean bean : posts){
                list.add(new PostPresenter(bean));
            }
        }

        @Override
        public Collection getList() {
            return list;
        }
    }

}
