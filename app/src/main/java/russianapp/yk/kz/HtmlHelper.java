package russianapp.yk.kz;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlHelper {
    TagNode rootNode;

    // Конструктор
    public HtmlHelper(URL htmlPage) throws IOException {
        // Создаём объект HtmlCleaner
        HtmlCleaner cleaner = new HtmlCleaner();
        // Загружаем html код сайта
        rootNode = cleaner.clean(htmlPage);
    }

    List<TagNode> getLinksByClass(String CSSClassname) {
        List<TagNode> linkList = new ArrayList<TagNode>();
        TagNode linkElements1[] = rootNode.getElementsByName("a", true);
        TagNode resPages = null;
        for (int i = 0; linkElements1 != null && i < linkElements1.length; i++) {
            //String classType1 = linkElements1[i].getAttributeByName("href");// /announce/show/...
            String classType2 = linkElements1[i].getParent().getParent()
                    .getAttributeByName("class"); // pages_box
            if (classType2 != null && classType2.equals("pages_box")
                // && classType1.startsWith("/announce/show/")
                    ) {
                resPages = linkElements1[i];
            }
        }

        linkList.add(resPages);

        // linkList = new ArrayList<TagNode>();
        TagNode linkElements[] = rootNode.getElementsByName("span", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++) {
            String classType1 = linkElements[i].getAttributeByName("class");
            if (classType1 != null && classType1.equals("news_date_sm")) {
                linkList.add(linkElements[i]);
            }
        }
        return linkList;
    }
}