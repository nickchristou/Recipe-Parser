package recipeparser.recipe;

import recipeparser.recipe.Recipe.Metadata;

/**
 * Output for first line read - Metadata plus recipe id
 * 
 * @author NicholasChristou
 *
 */
public class MetaDataAndId {
	
	private int id;
	private Metadata metadata;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
}
