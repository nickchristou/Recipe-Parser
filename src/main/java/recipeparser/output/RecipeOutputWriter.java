package recipeparser.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import recipeparser.recipe.Recipe;

public class RecipeOutputWriter {
	
	/**
	 * Polls through list of recipes and writes each to individual xml file
	 * 
	 * @param recipes - recipe list
	 * @param outputDir - output directory
	 * @return - number of xmls created
	 */
	public static int parseAllRecipes(List<Recipe> recipes, File outputDir) 
	{
		int filesPrinted = 0;
		for (Recipe recipe : recipes) {
			if (recipeToXml(recipe, outputDir)) 
			{
				filesPrinted++;
			}
			else
			{
				System.out.println("Failed to write to XML - Recipe ID: " + recipe.getId());
			}
		}
		
		return filesPrinted;		
	}

	/**
	 * Writes Recipe to XML File
	 * 
	 * @param recipe - recipe object
	 * @param outputDir - target output dir
	 * @return - true if success
	 */
	private static boolean recipeToXml(Recipe recipe, File outputDir) {
		
		//Filename created from id (consistent with expected output)
        File outputFile = new File(outputDir.getAbsolutePath() + 
        		File.separator + recipe.getId() + ".xml");
		
        //XML Writer
	       try (FileWriter writer = new XmlWriter(outputFile))
	        {
	    	   //Setup
	            JAXBContext jaxbContext = JAXBContext.newInstance(Recipe.class);
	            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
	            //Format consistent with expected output
	            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	            jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", false);
	 	            
	            //Write XML Recipe to output file
	            jaxbMarshaller.marshal(recipe, writer);
	            return true;
	            
	        } 
	       catch (JAXBException | IOException e) 
	       {
	    	   System.out.println("Failed to write to XML: " + outputFile.getName() + " - " + e.getMessage());
	    	   return false;
	        }
	}
	
}
