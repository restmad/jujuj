package framework.inj.entity;

public abstract class Follower<T> implements Downloadable{

    private T following;

    public T getFollowing(){
        return following;
    }

    public void setFollowing(T t){
        this.following = t;
    }
}
