package framework.inj.entity.utility;

/**
 * TODO not well designed
 * @author ss
 * an Injectable is an object to transfer object between different types
 */
public interface Transformable {

	/**
	 * get the real object
	 * @return
	 */
	public Object fromServer(String fieldName, Object value);
	
	public Object toServer(String fieldName, Object value);
	
}
