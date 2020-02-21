package chat;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

public class TextTest {
	
	@Test
	public void teste() {
		String unEscapedString = "<java>public static ç void main(String[] args) { ... }</java>";
        
        String escapedHTML = StringEscapeUtils.escapeHtml4(unEscapedString);
         
        System.out.println(escapedHTML);    //Browser can now parse this and print
	}
}
