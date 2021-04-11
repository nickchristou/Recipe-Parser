package recipeparser;

import java.io.File;
import java.util.List;

import recipeparser.input.RecipeFileReader;
import recipeparser.output.RecipeOutputWriter;
import recipeparser.recipe.Recipe;

/**
 * Recipe Parser App
 * 
 * @author NicholasChristou
 *
 */
public class RecipeParser {
	
 /**
  * Entry Point
  * 
  * @param args: 0 - inputDir, 1 - outputDir
  */
public static void main(String[] args) {
	  
	  //Check correct number of args
	  if (args.length != 2) {
		  System.out.println("Requires 2 args: [InputDir] [OutputDir]");
		  return;
	  }
	  
	  //Init
	  File inputDir = new File(args[0]);
	  File outputDir = new File(args[1]);
	  boolean invalidArgs = false;
	  
	  //Check input directory valid
	  if (!inputDir.isDirectory())
	  {
		  System.out.println("Input path not valid - " + args[0]);
		  invalidArgs = true;
	  }
	  
	  //Check output directory valid
	  if (!outputDir.isDirectory())
	  {
		  System.out.println("Output path not valid - " + args[1]);
		  invalidArgs = true;
	  }
	  
	  //Exit if directories not valid
	  if(invalidArgs)
		  return;
	  
	  //Actual work
	  int processedFiles = processFiles(inputDir, outputDir);
	  
	  //Complete
	  System.out.println("Recipe Parsing Complete. Files output: " + processedFiles);
  }
  
 /**
  * Reads txt files from input dir and print xml to output dir
  * 
  * @param inputDir - input directory
  * @param outputDir - output directory
  * @return - successfully created xmls
  */
public static int processFiles(File inputDir, File outputDir) 
  {
	  //Read files from input
	  List<Recipe> recipes = RecipeFileReader.readFilesFromInput(inputDir);
	  
	  //Write to XML from output
	  return RecipeOutputWriter.parseAllRecipes(recipes, outputDir);
  }
  
}