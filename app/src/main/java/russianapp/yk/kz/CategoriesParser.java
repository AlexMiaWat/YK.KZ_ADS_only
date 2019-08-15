package russianapp.yk.kz;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class CategoriesParser {
   TagNode rootNode;

   public CategoriesParser(URL htmlPage) throws IOException
   {
     HtmlCleaner cleaner = new HtmlCleaner();
     rootNode = cleaner.clean(htmlPage);
   }

   List<TagNode> getLinksByClass()
   {
     List<TagNode> linkList = new ArrayList<TagNode>();     
     TagNode linkElements1[] = rootNode.getElementsByName("img", true);
     for (int i = 0; linkElements1 != null && i < linkElements1.length; i++)
     {       	 
	       String classType1 = linkElements1[i].getAttributeByName("src");
	       String classType2 = linkElements1[i].getParent().getAttributeByName("href");
	       if (classType1 != null && classType1.equals("/img_f/item.gif") && classType2.startsWith("/announce"))
	       {
	    	   linkList.add(linkElements1[i].getParent());			    	 	    	    	 
	       } 	    	       
     }
     return linkList;
   }
 }