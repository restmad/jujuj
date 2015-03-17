package framework.inj.entity;

/**
 * TODO not well designed
 * @author ss
 * an Injectable is an object to transfer object between different types
 */
public interface Injectable {

	/**
	 * get the real object
	 * @return
	 */
	public Object getObject();
	
}
