package framework.inj.entity.utility;

public interface PostButtonListenable {

    void listen(Listener l);

    interface Listener{
        void onClick();
    }
}
