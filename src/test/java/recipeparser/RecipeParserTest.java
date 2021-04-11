package recipeparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.Test;

import recipeparser.input.RecipeFileReader;
import recipeparser.input.RecipeFileReaderUtils;
import recipeparser.output.RecipeOutputWriter;
import recipeparser.recipe.MetaDataAndId;
import recipeparser.recipe.ObjectFactory;
import recipeparser.recipe.Recipe;
import recipeparser.recipe.Recipe.Content;
import recipeparser.recipe.Recipe.Content.Ingredients.Ingredient;
import recipeparser.recipe.Recipe.Metadata;

public class RecipeParserTest {

private static File testResourceDir = new File("src/test/resources");
private static File testResourceOutputDir = new File("src/test/resources/Output");

	
	/**
	 * Assert correct number of files output
	 */
	@Test
	public void processFilesTest() 
	{
		assertEquals("Expected 5 files output", 5, RecipeParser.processFiles(testResourceDir, testResourceOutputDir));
	}
	
	/**
	 * Assert correct data in recipes read (Ids and Titles)
	 */
	@Test
	public void readFilesFromInputTest()
	{
		List<Recipe> recipes = RecipeFileReader.readFilesFromInput(testResourceDir);
	
		//number read
		assertEquals("Expect 5 recipes read", 5, recipes.size());
		
		//IDs
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getId() == 1).count());
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getId() == 21).count());
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getId() == 62).count());
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getId() == 63).count());
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getId() == 951).count());

		//Titles
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getMetadata().getTitle().equals("Lemon Cake")).count());
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getMetadata().getTitle().equals("Sponge Cake")).count());
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getMetadata().getTitle().equals("Mince Pies")).count());
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getMetadata().getTitle().equals("Carrot Cake")).count());
		assertEquals("Expect ID of 1 present", 1, recipes.stream().filter(r -> r.getMetadata().getTitle().equals("Raspberry Bakewell Cake")).count());
		
	}
	
	/**
	 * Verify multiple formats of first line read
	 */
	@Test
	public void firstLineScanTest()
	{
		RecipeFileReaderUtils utils = new RecipeFileReaderUtils();
		assertNull("Expect return of null when invalid", utils.firstLineScan("NONSENSE DATA"));
		assertNull("Expect return of null when invalid", utils.firstLineScan("NOTNUMERICID. TITLE"));

		//Recipe with id only on first line, expectation would be that this still scans but with empty metadata
		MetaDataAndId onePeriod = utils.firstLineScan("1.");
		assertEquals("Expect id read", 1, onePeriod.getId());
		assertTrue("Expect empty metadata", onePeriod.getMetadata().getTitle().isEmpty());
		assertNull("Expect empty metadata", onePeriod.getMetadata().getAuthor());
		assertNull("Expect empty metadata", onePeriod.getMetadata().getCreated());

		//Sample input line
		MetaDataAndId sampleInput = utils.firstLineScan("1. Lemon Cake by Sam Thompson [2018-12-03]");
		assertEquals("Expect id read", 1, sampleInput.getId());
		assertEquals("Expect sample output title", "Lemon Cake", sampleInput.getMetadata().getTitle());
		assertEquals("Expect sample output author", "Sam Thompson", sampleInput.getMetadata().getAuthor());
		assertEquals("Expect sample output date", 2018, sampleInput.getMetadata().getCreated().getYear());
		assertEquals("Expect sample output date", 12, sampleInput.getMetadata().getCreated().getMonth());
		assertEquals("Expect sample output date", 3, sampleInput.getMetadata().getCreated().getDay());

		//No date
		MetaDataAndId sampleInputNoDate = utils.firstLineScan("1. Lemon Cake by Sam Thompson");
		assertEquals("Expect id read", 1, sampleInputNoDate.getId());
		assertEquals("Expect sample output title", "Lemon Cake", sampleInputNoDate.getMetadata().getTitle());
		assertEquals("Expect sample output author", "Sam Thompson", sampleInputNoDate.getMetadata().getAuthor());
		assertNull("Expect no date", sampleInputNoDate.getMetadata().getCreated());
		
		//No author
		MetaDataAndId sampleInputNoAuthor = utils.firstLineScan("1. Lemon Cake [2018-12-03]");
		assertEquals("Expect id read", 1, sampleInputNoAuthor.getId());
		assertEquals("Expect sample output title", "Lemon Cake", sampleInputNoAuthor.getMetadata().getTitle());
		assertNull("Expect sample output author", sampleInputNoAuthor.getMetadata().getAuthor());
		assertEquals("Expect sample output date", 2018, sampleInputNoAuthor.getMetadata().getCreated().getYear());
		assertEquals("Expect sample output date", 12, sampleInputNoAuthor.getMetadata().getCreated().getMonth());
		assertEquals("Expect sample output date", 3, sampleInputNoAuthor.getMetadata().getCreated().getDay());
		
		//date before author
		MetaDataAndId sampleInputReordered = utils.firstLineScan("1. Lemon Cake [2018-12-03] by Sam Thompson");
		assertEquals("Expect id read", 1, sampleInputReordered.getId());
		assertEquals("Expect sample output title", "Lemon Cake", sampleInputReordered.getMetadata().getTitle());
		assertEquals("Expect sample output author", "Sam Thompson", sampleInputReordered.getMetadata().getAuthor());
		assertEquals("Expect sample output date", 2018, sampleInputReordered.getMetadata().getCreated().getYear());
		assertEquals("Expect sample output date", 12, sampleInputReordered.getMetadata().getCreated().getMonth());
		assertEquals("Expect sample output date", 3, sampleInputReordered.getMetadata().getCreated().getDay());
		
		//Invalid date format (CURRENTLY DD/MM/YYYY WOULD NOT BE VALID)
		MetaDataAndId sampleInputBadDate = utils.firstLineScan("1. Lemon Cake [03/12/2018] by Sam Thompson");
		assertEquals("Expect id read", 1, sampleInputBadDate.getId());
		assertEquals("Expect sample output title", "Lemon Cake", sampleInputBadDate.getMetadata().getTitle());
		assertEquals("Expect sample output author", "Sam Thompson", sampleInputBadDate.getMetadata().getAuthor());
		assertNull("Expect no date", sampleInputBadDate.getMetadata().getCreated());

		//Weird square brackets (ensure this does not confuse date logic)
		MetaDataAndId sampleInputBadDate2 = utils.firstLineScan("1. Lemon Cake ][ by Sam Thompson");
		assertEquals("Expect id read", 1, sampleInputBadDate2.getId());
		assertEquals("Expect sample output title (including extra chars)", "Lemon Cake ][", sampleInputBadDate2.getMetadata().getTitle());
		assertEquals("Expect sample output author", "Sam Thompson", sampleInputBadDate2.getMetadata().getAuthor());
		assertNull("Expect no date", sampleInputBadDate2.getMetadata().getCreated());
		
	}
	
	/**
	 * Assert string parse to int as required by app
	 */
	@Test
	public void tryParseIntTest()
	{
		RecipeFileReaderUtils utils = new RecipeFileReaderUtils();

		//positive
		assertEquals("Expect int output", Integer.valueOf(5), utils.tryParseInt("5"));
		
		//preceding 0's
		assertEquals("Expect int output", Integer.valueOf(5), utils.tryParseInt("0005"));

		//negative
		assertEquals("Expect int output", Integer.valueOf(-5), utils.tryParseInt("-5"));

		//chars
		assertNull("Expect int output", utils.tryParseInt("abc"));
		
		//char/num mix
		assertNull("Expect int output", utils.tryParseInt("55abc"));

	}
	
	/**
	 * Assert conversion of special 1/2 char
	 */
	@Test
	public void convertHalfCharTest()
	{
		RecipeFileReaderUtils utils = new RecipeFileReaderUtils();

		//Basic convert
		assertEquals("Expect .5", ".5", utils.convertHalfChar("½"));
		
		//No convert required
		assertEquals("Expect returned with no change", "Test", utils.convertHalfChar("Test"));
		
		//Combo
		assertEquals("Expect 1.5-2", "1.5-2", utils.convertHalfChar("1½-2"));
	}
	
	/**
	 * Assert conversion of string to double amounts (as required by app)
	 */
	@Test
	public void tryParseIngAmtTest()
	{
		RecipeFileReaderUtils utils = new RecipeFileReaderUtils();
		
		//Not valid
		assertNull("Expect null", utils.tryParseIngAmt("TEST"));
		
		//int
		assertEquals("1", Double.valueOf(1), utils.tryParseIngAmt("1"));
		
		//Fraction
		assertEquals("Expect null", Double.valueOf(0.25), utils.tryParseIngAmt("1/4"));
		
		//Range given (expect take first)
		assertEquals("Expect 1.5" , Double.valueOf(1.5), utils.tryParseIngAmt("1.5-2"));
	}
	
	/**
	 * Assert reading of number of ingredients and checks elements
	 */
	@Test
	public void readIngredientLineTest()
	{
		RecipeFileReaderUtils utils = new RecipeFileReaderUtils();

		//Ingredient scans
		Ingredient sample1 = utils.readIngredientLine("225g unsalted butter");
		Ingredient sample2 = utils.readIngredientLine("20ml milk");
		Ingredient fourEggs = utils.readIngredientLine("4 eggs");
		Ingredient itemOnly = utils.readIngredientLine("icing sugar, to dust");
		Ingredient fractionalAmt = utils.readIngredientLine("1/2 lemon zested");
		
		//sample 1 assertions
		assertEquals("Expect 225", 225.0d, sample1.getAmount(), 0.0d);
		assertEquals("Expect grams", "grams", sample1.getUnit());
		assertEquals("Expect unsalted butter", "unsalted butter", sample1.getItem());
		
		//sample 2 assertions
		assertEquals("Expect 20", 20.0d, sample2.getAmount(), 0.0d);
		assertEquals("Expect millilitres", "millilitres", sample2.getUnit());
		assertEquals("Expect milk", "milk", sample2.getItem());
		
		//fourEggs assertions
		assertEquals("Expect 4", 4.0d, fourEggs.getAmount(), 0.0d);
		assertNull("Expect null", fourEggs.getUnit());
		assertEquals("Expect eggs", "eggs", fourEggs.getItem());
		
		//itemOnly assertions
		assertNull("Expect null", itemOnly.getAmount());
		assertNull("Expect null", itemOnly.getUnit());
		assertEquals("Expect icing sugar, to dust", "icing sugar, to dust", itemOnly.getItem());
		
		//fractionalAmt assertions
		assertEquals("Expect half", 0.5d, fractionalAmt.getAmount(), 0.0d);
		assertNull("Expect null", fractionalAmt.getUnit());
		assertEquals("Expect lemon zested", "lemon zested", fractionalAmt.getItem());
	}
	
	/**
	 * Assert step lines read with and without numbering
	 */
	@Test
	public void readMethodStepLineTest()
	{
		RecipeFileReaderUtils utils = new RecipeFileReaderUtils();
		
		//Sample I/O
		assertEquals("Expect sample output", "Bake for 45-50 minutes.", utils.readMethodStepLine("6. Bake for 45-50 minutes."));
		
		//No numbering
		assertEquals("Expect return equal string", "Heat oven to 180C/160C fan/gas 4 and base-line and grease a deep 20cm loose-bottomed cake tin. Blitz the ground almonds, butter, sugar, flour, eggs and vanilla extract in a food processor until well combined.", utils.readMethodStepLine("Heat oven to 180C/160C fan/gas 4 and base-line and grease a deep 20cm loose-bottomed cake tin. Blitz the ground almonds, butter, sugar, flour, eggs and vanilla extract in a food processor until well combined."));
	}
	
	/**
	 * Check numbering element returned from string (or null if not present)
	 */
	@Test
	public void checkNumberingTest()
	{
		RecipeFileReaderUtils utils = new RecipeFileReaderUtils();

		//Sample input numbering
		assertEquals("Expect numbering extracted", "6.", utils.checkNumbering("6. Bake for 45-50 minutes."));

		//No numbering
		assertNull("Expect null return", utils.checkNumbering("Heat oven to 180C/160C fan/gas 4 and base-line and grease a deep 20cm loose-bottomed cake tin. Blitz the ground almonds, butter, sugar, flour, eggs and vanilla extract in a food processor until well combined."));
		
		//Alt numbering format
		assertEquals("Expect numbering extracted", "6)", utils.checkNumbering("6) Bake for 45-50 minutes."));
	
	}
	
	/**
	 * Test parsing of basic dummy recipe obj
	 */
	@Test
	public void parseAllRecipesTest()
	{
		//Init 1 obj
		List<Recipe> testList = new ArrayList<>();
		Recipe r = createTestRecipe();
		testList.add(r);
		
		//One file parse
		assertEquals("Expect successful parsing of one file", 1, RecipeOutputWriter.parseAllRecipes(testList, testResourceOutputDir));
	}
	
	/**
	 * @return - dummy recipe object
	 */
	private Recipe createTestRecipe()
	{
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
		ObjectFactory factory = new ObjectFactory();
		
		Recipe recipe = factory.createRecipe();
		
		//Metadata & content init
		Metadata metadata = factory.createRecipeMetadata();
		Content content = factory.createRecipeContent();
		recipe.setMetadata(metadata);
		recipe.setContent(content);
				
		//Metadata info and Id
		recipe.setId(1);
		recipe.getMetadata().setTitle("TEST");
		recipe.getMetadata().setAuthor("TEST AUTHOR");
	
		//Date element
		try 
		{
			Date date = format.parse("2014-04-24");
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			recipe.getMetadata().setCreated(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
		}
		catch (ParseException | DatatypeConfigurationException e)
		{
			System.out.println("DATE FAILED");
			e.printStackTrace();
		}
	
		//Ingredient
		Recipe.Content.Ingredients.Ingredient ingredient = factory.createRecipeContentIngredientsIngredient();
		ingredient.setAmount(5d);
		ingredient.setItem("Apple");
		ingredient.setUnit("each");		
		
		//Content
		recipe.getContent().setLead("");
		recipe.getContent().getIngredients().getIngredient().add(ingredient);
		recipe.getContent().getMethod().getStep().add("This is the first step in test");
		recipe.getContent().getMethod().getStep().add("This is the second step in test");

	return recipe;
	}
	
}
