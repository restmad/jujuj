package framework.inj.entity.utility;

public interface Validatable {

	/**
	 * 
	 * @param field
	 * @return null if validate
	 */
	public String validate(String field, String value);
	
}
