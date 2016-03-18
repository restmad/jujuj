package framework.inj.entity;

public interface Following<T extends Follower>{

    void onFollowerRecall(T t);
}
