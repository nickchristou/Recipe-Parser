//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.04.09 at 07:19:42 PM BST 
//


package recipeparser.recipe;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Recipe }
     * 
     */
    public Recipe createRecipe() {
        return new Recipe();
    }

    /**
     * Create an instance of {@link Recipe.Content }
     * 
     */
    public Recipe.Content createRecipeContent() {
        return new Recipe.Content();
    }

    /**
     * Create an instance of {@link Recipe.Content.Ingredients }
     * 
     */
    public Recipe.Content.Ingredients createRecipeContentIngredients() {
        return new Recipe.Content.Ingredients();
    }

    /**
     * Create an instance of {@link Recipe.Metadata }
     * 
     */
    public Recipe.Metadata createRecipeMetadata() {
        return new Recipe.Metadata();
    }

    /**
     * Create an instance of {@link Recipe.Content.Method }
     * 
     */
    public Recipe.Content.Method createRecipeContentMethod() {
        return new Recipe.Content.Method();
    }

    /**
     * Create an instance of {@link Recipe.Content.Ingredients.Ingredient }
     * 
     */
    public Recipe.Content.Ingredients.Ingredient createRecipeContentIngredientsIngredient() {
        return new Recipe.Content.Ingredients.Ingredient();
    }

}
