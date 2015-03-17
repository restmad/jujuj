package framework.inj.entity;

public class MutableEntity<T> {
	
	public MutableEntity(T t){
		this.t = t;
	}

	private T t;
	
	public T getEntity(){
		return t;
	}
	
	public void setEntity(T t){
		this.t = t;
	}
	
	protected boolean bStored = false;

	public boolean isStateStored() {
		return bStored;
	}

	public void onStoring() {
		bStored = true;
	}
}
