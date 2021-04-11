package recipeparser.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Format XML Output
 * 
 * @author NicholasChristou
 *
 */
public class XmlWriter extends FileWriter {
    public XmlWriter(File file) throws IOException {
        super(file);
    }

    /**
     * Removes blank line due to removal of xml declaration tag
     */
    public void write(String str) throws IOException {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(str)) {
            super.write(str);
        }
    }
}
