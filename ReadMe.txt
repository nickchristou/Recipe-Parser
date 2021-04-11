*********************************************
*********************************************
*********************************************

RecipeParser Application by Nicholas Christou

*********************************************
*********************************************
*********************************************

Language Selection:

Opted to create a java based app so I could use JAXB to handle XML marshalling and use xjc to generate classes
automatically from xsd created from the original sample output xml. I have then made minor modifications to these classes
and xsd as required to suit application functionality and testing.

How to use:

1. Set up an input directory containing recipe txt files and output directory to write XML to.
2. Execute "runRecipeParser.bat".
3. Enter input directory (eg. on my system this was "C:\RecipeTarget\Input").
4. Enter output directory (eg. on my system this was "C:\RecipeTarget\Output").
5. Application will read from input directory, only reading files with ".txt" extension and ignoring sub-directories.
6. Output will write to the directory with following filename format: {ID}{Title}.xml, if recipe already exists this will overwrite existing files.

FileSpec:

File spec is included in root directory.

For the most part I've remained relatively faithful to the input/output provided. 
However I've opted to write ingredient amount as a decimal value as opposed to an int.
I believe this allows for larger amount of ingredient amounts to be successfully processed (eg. ingredient line listing 0.5kg of flour).
If hypothetically it was a hardline requirement by the client for this to instead be int, this could be reverted and decimal values prevented from scanning.
Doing this would reduce the number of recipe file lines ingredient amounts that could be successfully scanned (½ or 1/2, values would need to be stripped out).

Elements are read accordingly:

-First non empty line MUST contain recipe id; then optionally title, author and create date
-Integer value before "." in first line is read as the recipe id (required field)
-Text after "by" and before Date (if it exists) is read as the author
-Text contained within square brackets in date format "[YYYY-MM-DD]" is read as the create date
-Any other text on the first line is read as the title (required field)
-Any text content read before "Ingredients" is read as lead content
-Lines read after "Ingredients" are read as individual ingridients
-Numeric value at start required, read as amount
-Any characters attached to amount value assessed as unit of measure
-Text afterwards on line read as Item
-Lines read after "Method" read as individual steps
-Step text read, removing step number if present

Future Improvements:

If investing more time into the project, I'd look at implementing the following improvements:

-Refactoring + Reorganisation
-Output file validation against schema for conformence
-Improved unit of measure recognition
-Improved ingredient recognition
-Improved date parsing (accept multiple formats)
-Reason codes for parsing failiures (report to cmd which element failed)