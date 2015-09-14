package framework.inj.entity;

import framework.inj.entity.utility.Notifiable;

/**
 * A MutableEntity is just like a shell,
 * which contains a entity that needs to load entity from somewhere else(either local or server)
 *
 * @param <T> the entity that is supposed to load data from somewhere(either local or server)
 */
public class MutableEntity<T> {

	public MutableEntity(){}

	public MutableEntity(T t){
		this.target = t;
	}

	public MutableEntity(T t, Notifiable notifiable){
		this.target = t;
		this.notifiable = notifiable;
	}

	private T target;
	private Notifiable notifiable;
	
	public Notifiable getNotifiable(){
		return notifiable;
	}
	
	public T getEntity(){
		return target;
	}
	
	public void setEntity(T t){
		this.target = t;
	}
	
	protected boolean bStored = false;

	public boolean isStateStored() {
		return bStored;
	}

	public void onStoring() {
		bStored = true;
	}
	
	public void cancelStoring() {
		bStored = false;
	}
}
