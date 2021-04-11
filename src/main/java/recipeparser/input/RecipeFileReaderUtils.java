package recipeparser.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import recipeparser.recipe.AmountAndUom;
import recipeparser.recipe.MetaDataAndId;
import recipeparser.recipe.ObjectFactory;
import recipeparser.recipe.Recipe;
import recipeparser.recipe.Recipe.Content.Ingredients.Ingredient;
import recipeparser.recipe.Recipe.Metadata;

public class RecipeFileReaderUtils {
	
	/**
	 * Generates blank recipe
	 * 
	 * @return - blank recipe
	 */
	protected static Recipe createNewBlankRecipe() {
		ObjectFactory factory = new ObjectFactory();
		Recipe recipe = factory.createRecipe();
		return recipe;
	}

	/**
	 * Scan method for first non-empty line of recipe file specifically.
	 * This will retrieve the metadata info as well as the recipe ID
	 * 
	 * @param s - input string
	 * @return - metadata and id (id and title required), null if invalid
	 */
	public MetaDataAndId firstLineScan(String s) {
		
		//Init
		ObjectFactory factory = new ObjectFactory();
		MetaDataAndId m = new MetaDataAndId();
		Metadata meta = factory.createRecipeMetadata();
		String trimmedContent = s.trim();
		
		//Index setup
		int periodIndex = trimmedContent.indexOf(".");
		
		//ID - Required field
		if (periodIndex == -1) 
		{
			System.out.println("Failed to read ID from first line: " + s);
			return null;
		}
		
		int byIndex = trimmedContent.indexOf(" by ");
		int dateOpenIndex = trimmedContent.lastIndexOf("[");
		int dateCloseIndex = trimmedContent.lastIndexOf("]");

		//Set ID
		Integer id = tryParseInt(trimmedContent.substring(0, periodIndex));
		if (id != null)
			m.setId(id);
		else 
		{
			System.out.println("Failed to read ID from first line: " + s);
			return null;
		}
		
		//Metadata
		if (periodIndex != -1) 
		{
			//Author
			meta.setAuthor(retrieveAuthor(trimmedContent, byIndex, dateOpenIndex, dateCloseIndex));
			//Title
			meta.setTitle(retrieveTitle(trimmedContent, periodIndex, byIndex, dateOpenIndex, dateCloseIndex));
			//Created Date
			if(dateOpenIndex != -1 && dateCloseIndex != -1)
				meta.setCreated(retrieveCreatedDate(trimmedContent, dateOpenIndex, dateCloseIndex));
		}

		//Require at minimum id or title
		if (meta.getTitle() == null)
			return null;
		
		//Set metadata & return
		m.setMetadata(meta);
		return m;
		
	}
	
	/**
	 * Retrieves title from first line
	 * 
	 * @param trimmedContent - trimmed input string
	 * @param byIndex - index of "by"
	 * @param dateOpenIndex - index of "["
	 * @param dateCloseIndex - index of "]"
	 * @param  periodIndex - index of "." (end of id)
	 * @return Title info, or null if invalid
	 */
	private String retrieveTitle(String trimmedContent, int periodIndex, int byIndex, int dateOpenIndex, int dateCloseIndex) 
	{
		//Evaluate if date present (false if edge case where ] before [)
		boolean datePresent = false;
		if (dateOpenIndex != -1 && dateOpenIndex < dateCloseIndex)
			datePresent = true;
		
		//No author or date
		if (byIndex == -1 && !datePresent)
			return trimmedContent.substring(periodIndex + 1).trim();
		
		//No author or date before author
		if ( datePresent && (byIndex == -1 || dateOpenIndex < byIndex))
			return trimmedContent.substring(periodIndex + 1, dateOpenIndex).trim();
		
		//No date or author before date
		if ((byIndex != -1 && !datePresent) || (datePresent && byIndex < dateOpenIndex))
			return trimmedContent.substring(periodIndex + 1, byIndex).trim();
		
		//If somehow none of the above, just return null (no title found). However expectation would be empty string returned.
		return null;
	}

	/**
	 * Retrieve date from input string
	 * 
	 * @param trimmedContent - trimmed input string
	 * @param dateOpenIndex - index of "["
	 * @param dateCloseIndex - index of "]"
	 * @return date, or null if invalid
	 */
	private XMLGregorianCalendar retrieveCreatedDate(String trimmedContent, int dateOpenIndex, int dateCloseIndex) 
	{
		//Return null if no date or in edge case where ] occurs before [
		if (dateOpenIndex != -1 && dateCloseIndex < dateOpenIndex)
			return null;
		
		//Init
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = trimmedContent.substring(dateOpenIndex+1, dateCloseIndex).trim();
		Date date;
		
		//Attempt parse to XMLGregorianCalander
		try {
			date = format.parse(dateString);

			//Calander init
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			
			//XMLCalander setup (no timezone for conformence to sample output)
			XMLGregorianCalendar xmlCal =  DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			xmlCal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			
			return xmlCal;
		} 
		//Return null if failed
		catch (ParseException | DatatypeConfigurationException e) 
		{
			System.out.println("Failed to parse date" + " - " + e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieve author information from file
	 * 
	 * @param trimmedContent - trimmed string input
	 * @param byIndex - index of "by" in first line (if present)
	 * @param dateOpenIndex - index of "[" in first line (if present)
	 * @param dateCloseIndex - index of "]" in first line (if present)
	 * @return - Author info, or null if not found
	 */
	private String retrieveAuthor(String trimmedContent, int byIndex, int dateOpenIndex, int dateCloseIndex) 
	{
		if (byIndex != -1) 
		{
			//date after author
			if (dateOpenIndex != -1 && dateCloseIndex != -1 && dateOpenIndex > byIndex)
				return trimmedContent.substring(byIndex + 4, dateOpenIndex).trim();
			//author after date or no date
			else
				return trimmedContent.substring(byIndex + 4).trim();
		}
		//No author found
		else
			return null;
	}

	/**
	 * Parse string as int or return null if invalid
	 * 
	 * @param value - string input
	 * @return - parsed int or null
	 */
	public Integer tryParseInt(String value) {
	    try {
	        return Integer.parseInt(value);
	    } catch (NumberFormatException e) {
	        return null;
	    }
	}
	
	/**
	 * Replaces any special half char with .5
	 * 
	 * @param value - string to convert
	 * @return - replaced string
	 */
	public String convertHalfChar(String value) 
	{		
		//replace special char with .5 (to convert to half)
		if (value.contains("½")) 
			return value.replace("½", ".5");
		else
			return value;
	}
	
	/**
	 * Parse string as double or return null if invalid (include fraction format)
	 * 
	 * @param value - string to convert
	 * @return - converted double or null if invalid
	 */
	public Double tryParseIngAmt(String value) 
	{
		
		//Strip out hyphen (do not allow ranges, take first instead)
		if (value.contains("-"))
			value = value.substring(0, value.indexOf("-"));
		
		//Fraction format
		if (value.contains("/"))
		{
			//Split numerator from denominator
			String[] fraction = value.split("/");
			
			//If not split into at least 2, return null
			if (fraction.length < 2)
				return null;
			
			//Try parse with fraction amount
			try 
			{
				return Double.parseDouble(fraction[0]) / Double.parseDouble(fraction[1]);
	    	} 
			catch (NumberFormatException e) 
			{
	    		return null;
	    	}
		}
		
		//Standard try parse
		else 
		{
			try 
			{
				return Double.parseDouble(value);
	    	} 
			catch (NumberFormatException e) 
			{
	    		return null;
	    	}
		}
	}

	/**
	 * Reads individual ingredient element from input line
	 * 
	 * @param s - input string
	 * @return - Ingredient or null if invalid
	 */
	public Ingredient readIngredientLine(String s) 
	{
		//Init
		ObjectFactory factory = new ObjectFactory();
		Ingredient ing = factory.createRecipeContentIngredientsIngredient();
		String trimmedContent = s.trim();
		String item = null;

		//Split into words, first word is expected to be amount+uom or amount only with 2nd being uom only
		String[] words = Pattern.compile("\\s+").split(trimmedContent);
				
		//Return null if nothing found
		if (words.length == 0)
			return null;
		
		//if only length of 1, this is likely to be only an item (eg. food colouring)
		if (words.length == 1)
			ing.setItem(words[0]);
		
		else 
		{
			String firstEl = convertHalfChar(words[0]);
			String amountAndUom = null;
			String uomOnly = null;
		
			//Check if first word is numeric or null
			Double tryParseAmount = tryParseIngAmt(firstEl);
		
			//If not parsable, this is potentially due to uom attached
			if (tryParseAmount == null)
				amountAndUom = firstEl;
			//If this can be parsed then must be the amount
			else
			{
				ing.setAmount(tryParseAmount);
				uomOnly = words[1];//Note this may be uom or start of item
			}
			
			//If amount was attached (or potentially this is just item only)
			if (amountAndUom != null) 
			{
				AmountAndUom splitAmtAndUom = splitAmountFromUom(amountAndUom);
				Double tryParseIngAmt = tryParseIngAmt(splitAmtAndUom.getAmount());
				ing.setAmount(tryParseIngAmt);
				ing.setUnit(findUnitOfMeasure(splitAmtAndUom.getUom()));
				
				
				//Line must have ingredient item only
				if (tryParseIngAmt == null) 
				{
					item = "";
					for (String word : words) 
					{
						item += word + " ";
					}
				}
				//Skip the uom&amt element in index 0
				else 
				{
					item = "";
					for (int i=1; i<words.length; i++)
					{
						item += words[i] + " ";
					}
				}
				
			}
			
			//If amount and unit of measurement were seperate
			if (uomOnly != null)
			{
				String uom = findUnitOfMeasure(uomOnly);
				
				ing.setUnit(uom);
				item = "";
				
				if (uom != null) {
					//In this block, element 1 must have been amt and element 2 must have been uom
					//Therefore skipping to element 3+ will give the item
					for (int i=2; i<words.length; i++)
					{
						item += words[i] + " ";
					}
				}
				else 
				{
					//In this block, element 1 was amount but no valid unit of measurement not found
					//Therefore item starts @ 2 eg.(large eggs)
					for (int i=1; i<words.length; i++)
					{
						item += words[i] + " ";
					}
				}
			}
			
			//Finally set item
			ing.setItem(item.trim());
		}

		return ing;
	}

	/**
	 * Matches input string against predefined UOMs - null if not matching
	 * 
	 * This method could be expanded upon to support more uom types
	 * 
	 * @param uom - input string
	 * @return - matched uom
	 */
	private String findUnitOfMeasure(String uom) 
	{
		String unitOfMeasure = uom.toLowerCase();
		
		switch (unitOfMeasure)
		{
		//grams
		case "g":
		case "grams":
			return "grams";
		//kilograms
		case "kg":
		case "kilograms":
			return "kilograms";
		//drops
		case "dr":
		case "drop":
		case "drops":
			return "drops";
		//pinches
		case "pn":
		case "pinch":
		case "pinches":
			return "pinches";
		//teaspoons
		case "tsp":
		case "teaspoon":
		case "teaspoons":
			return "teaspoons";
		//tablespoons
		case "tbsp":
		case "tablespoon":
		case "tablespoons":
			return "tablespoons";
		//millilitres
		case "ml":
		case "millilitre":
		case "millilitres":
			return "millilitres";
		//litres
		case "l":
		case "litre":
		case "litres":
			return "litres";	
		//pints
		case "pt":
		case "pint":
		case "pints":
			return "pints";	
		//cups
		case "c":
		case "cup":
		case "cups":
			return "cups";	
		//Other (eg. each - therefore uom not required in xml)
		default:
			return null;
		
		}		
	}

	/**
	 * Splits string into amount (numeric) and uom (alphabetic chars)
	 * 
	 * @param amountAndUom - input string
	 * @return - amt and uom split strings
	 */
	private AmountAndUom splitAmountFromUom(String amountAndUom) {
		
		//Init
        StringBuffer uom = new StringBuffer(), amt = new StringBuffer();
        
        //Foreach char
        for (int i=0; i<amountAndUom.length(); i++)
        {
        	//Numeric into amt
            if (Character.isDigit(amountAndUom.charAt(i)))
                amt.append(amountAndUom.charAt(i));
            //alphabetic chars into uom
            else if (Character.isAlphabetic(amountAndUom.charAt(i)))
                uom.append(amountAndUom.charAt(i));
        }	
        
        return new AmountAndUom(amt.toString(), uom.toString());
        
	}

	/**
	 * Reads step line, stripping out numbering if present (conformence with sample output)
	 * 
	 * @param s - input string
	 * @return - processed step string
	 */
	public String readMethodStepLine(String s) {
		
		String trimmedContent = s.trim();
		String numbering = checkNumbering(trimmedContent);
		
		if (numbering != null)
			return trimmedContent.replace(numbering, "").trim();
		else
			return trimmedContent;
	}
	
	/**
	 * Checks if numbering is present at beginning of line and returns as string
	 * 
	 * @param s - input string
	 * @return - numbering including closing period or close bracket, or null if not present
	 */
	public String checkNumbering(String s) 
	{
		StringBuffer numbering = new StringBuffer();
		for (int i=0; i<s.length(); i++) 
		{
            //End of numbering denoted via . or )
            if(s.length() > i+1 && (s.substring(i, i+1).equals(".") || s.substring(i, i+1).equals(")")))
            {
            	numbering.append(s.charAt(i));
            	return numbering.toString();
            }
			
			//If not digit, then not a numbered line
			if (!Character.isDigit(s.charAt(i)))
				return null;
			
			//Append numbers in numbering
            if (Character.isDigit(s.charAt(i)))
            	numbering.append(s.charAt(i));

		}
		return null;
	}
	
    /**
     * Remove BOM char from string if present (prevents parsing exceptions)
     * 
     * @param s - input string
     * @return - removed BOM
     */
    public static String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        return s;
    }

}
