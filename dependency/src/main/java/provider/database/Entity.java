package provider.database;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.List;

public abstract class Entity extends Model{

	public Entity(){
		super();
	}
	
	public void setForeignKey(Entity entity){}

	public abstract Entity query();

	protected final <E extends Model> List<E> getMani(Class<? extends Model> type, String foreignKey) {
		return (new Select()).from(type).where(foreignKey + "=?", new Object[]{this.getId()}).execute();
	}
}
