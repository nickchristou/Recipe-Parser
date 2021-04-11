package recipeparser.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;

import recipeparser.recipe.MetaDataAndId;
import recipeparser.recipe.Recipe;

public class RecipeFileReader {

	/**
	 * Polls through files in input directory and retrieves recipes
	 * 
	 * @param inputDir - input directory argument
	 * @return - recipe list
	 */
	public static List<Recipe> readFilesFromInput(File inputDir) 
	{
		//Init
		List<Recipe> recipes = new ArrayList<>();
		List<String> failedRecipes = new ArrayList<>();
		
		  File[] directoryListing = inputDir.listFiles();
		  if (directoryListing != null) 
		  {
		    for (File child : directoryListing) 
		    {
		    	//Skip non-text files
		    	if(!FilenameUtils.getExtension(child.getAbsolutePath()).equals("txt")) {
		    		System.out.println("Skipping non-txt file: " + child.getName());
		    		continue;
		    	}
		    	
		    	//Read file
		    	Recipe recipe = readSingleRecipeFile(child);
		    	
		    	//Add if not null
		    	if (recipe != null)
		    		recipes.add(recipe);
		    	//Else add to failed list
		    	else
		    		failedRecipes.add(child.getName());
		    		
		    }
		  }
		  
		  //Report fails to console
		  if (failedRecipes.size() != 0)
		  {
			  System.out.println("Failed to read " + failedRecipes.size() + " files");
		  }
		return recipes;
	}

	/**
	 * Attempts to read individual text file to recipe, returns null if invalid
	 * 
	 * This method requires that ingredients in recipe are listed before the method, and that these
	 * sections are marked in the text file as either "Ingredients" and "Method" respectively.
	 * 
	 * @param recipeFile - file to read
	 * @return recipe or null if invalid
	 */
	private static Recipe readSingleRecipeFile(File recipeFile) {
		
		//Init recipe content
		Recipe recipe = RecipeFileReaderUtils.createNewBlankRecipe();
		RecipeFileReaderUtils recipeReader = new RecipeFileReaderUtils();
		boolean firstLineRead = false;
		boolean ingredientsLineHit = false;
		boolean methodLineHit = false;
		boolean leadFirstLineRead = false;
		
		//Read file lines
//		try (BufferedReader reader = new BufferedReader(new FileReader(recipeFile.getAbsoluteFile()))) 
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(recipeFile.getAbsoluteFile()), "UTF8"))) 
		{
			String s;
			String lead = null;
			while((s = reader.readLine()) != null) 
			{

				//Remove BOM if present
				s = RecipeFileReaderUtils.removeUTF8BOM(s);
				
				//Skip if blank
				if (s.isEmpty())
					continue;
				
				//Check for ingredients line
				if (StringUtils.containsIgnoreCase(s, "Ingredient"))
				{
					recipe.getContent().setLead(lead);
					ingredientsLineHit = true;
					continue;
				}
				
				//Check for method line
				if (StringUtils.containsIgnoreCase(s, "Method"))
				{
					methodLineHit = true;
					continue;
				}
				
				//if neither ingredients or method hit yet but first line has been read, then this is recipe lead (description)
				if (firstLineRead && !ingredientsLineHit && !methodLineHit)
				{
					//Initialise
					if (lead == null)
						lead = "";
					
					//Append to lead (no new line if not required to keep xml tidy)
					if(!leadFirstLineRead)
					{
						lead += s.trim();
						leadFirstLineRead = true;
					}
					//insert newline to preserve multi-line description
					else 
					{
						lead += System.lineSeparator() + s.trim();
					}
					
				}
				
				//First non empty line in text file
				if (!firstLineRead) 
				{
					
					MetaDataAndId m = recipeReader.firstLineScan(s);
					
					//If metadata is null, then id or title was not read so return null
					if (m == null)
						return null;
					
					//Set metadata values and ID
					recipe.setId(m.getId());
					recipe.setMetadata(m.getMetadata());
					
					//Flag first line read
					firstLineRead = true;
				}
				
				// if ingredients line hit but before method line hit
				if(ingredientsLineHit && !methodLineHit) 
				{
					recipe.getContent().getIngredients().getIngredient().add(recipeReader.readIngredientLine(s));
				}
				
				// if method line hit
				if(ingredientsLineHit && methodLineHit) 
				{
					recipe.getContent().getMethod().getStep().add(recipeReader.readMethodStepLine(s));
				}
				
			}//end while loop
			
			//If ingredients or method not found... or somehow first line not read... just return null
			if (!firstLineRead || !ingredientsLineHit || !methodLineHit )
			{
				return null;
			}
		}
		catch (IOException e) 
		{
			System.out.println("Failed to read file: " + recipeFile.getName() + " - " + e.getMessage());
			return null;
		}
		
		//return recipe
		return recipe;
	}


	
}
