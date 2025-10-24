package service.data;

import org.jsoup.Jsoup;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;


public class HtmlSanitizer {

  private static Safelist textEditorSafeList;

  private static Cleaner basicCleaner;
  static {
    textEditorSafeList = Safelist.relaxed().addAttributes(":all","class","id", "style");
    basicCleaner = new Cleaner(Safelist.none());
  }

  public static String sanitizeHtml(String html){
    if(html == null){
      return "";
    }
    return Jsoup.clean(html, textEditorSafeList);
  }

  public static String sanitizeTextOnly(String html) {
    if(html == null){
      return "";
    }
    return basicCleaner.clean(Jsoup.parse(html)).text();
  }
}
