package framework.inj.entity;

public class MutableEntity {
	
	public MutableEntity(Object t){
		this.t = t;
	}

	public MutableEntity(Object t, Notifiable notifiable){
		this.t = t;
		this.notifiable = notifiable;
	}

	private Object t;
	private Notifiable notifiable;
	
	public Notifiable getNotifiable(){
		return notifiable;
	}
	
	public Object getEntity(){
		return t;
	}
	
	public void setEntity(Object t){
		this.t = t;
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
