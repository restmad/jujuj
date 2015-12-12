package framework.inj.entity.utility;

public interface PostButtonListenable {

    public void listen(Listener l);

    public interface Listener{
        public void onClick();
    }
}
