package framework.inj.entity;

public interface Validatable {

	/**
	 * 
	 * @param field
	 * @return null if validate
	 */
	public String validate(String field, String value);
	
}
