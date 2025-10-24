package app.service.data;

import org.junit.jupiter.api.Test;
import service.data.HtmlSanitizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlSanitizerTest {

  /*
    Test script blocker logic.
    Test if service classers throw error when necessary
   */
  static final String HTML_A = "<h1>Test: A title </h1><p>Content</p>";
  static final String HTML_B = "<p class=\"Class_C\">Test: \"A title \" </p>";
  static final String HTML_C = "<p id=\"Id_D\">Test: \"A title \" </p>";
  static final String HTML_D = "<p style=\"font-size:bold\">Test: \"A title \" </p>";

  static final String HTML_E = "<ul style=\"list-style: url(/assets/meeple_small.png);\">\n" +
      "<li>Brass: Birmingham</li>\n" +
      "<li>Power Grid</li>\n" +
      "<li>Dune Imperium</li>\n" +
      "<li>Viticulture</li>\n" +
      "<li>Puerto Rico</li>\n" +
      "<li>Bomb Busters</li>\n" +
      "</ul>\n";

  @Test
  public void testValidHtml_No_Attributes_IsNotModified(){
    String sanitized = HtmlSanitizer.sanitizeHtml(HTML_A);
    assertHtmlEquals(HTML_A, sanitized);
  }

  @Test
  public void testValidHtml_ClassParam_IsNotModified(){
    String sanitized = HtmlSanitizer.sanitizeHtml(HTML_B);
    assertHtmlEquals(HTML_B, sanitized);
  }

  @Test
  public void testValidHtml_IdParam_IsNotModified(){
    String sanitized = HtmlSanitizer.sanitizeHtml(HTML_C);
    assertHtmlEquals(HTML_C, sanitized);
  }

  @Test
  public void testValidHtml_StyleParam_IsNotModified(){
    String sanitized = HtmlSanitizer.sanitizeHtml(HTML_D);
    assertHtmlEquals(HTML_D, sanitized);
  }

  @Test
  public void testValidHtml_StyleParamWithImage_IsNotModified(){
    String sanitized = HtmlSanitizer.sanitizeHtml(HTML_E);
    assertHtmlEquals(HTML_E, sanitized);
  }

  @Test
  public void testHtml_withClickHandler_clickHandlerIsRemoved(){
    String html = "<button onclick=\"(function() { alert ('hi') })()\">Hi</button>";
    String sanitized = HtmlSanitizer.sanitizeHtml(html);
    assertHtmlEquals("Hi",sanitized);
  }

  @Test
  public void test_sanitizeTextOnly_RemovesHtml(){
    String sanitized = HtmlSanitizer.sanitizeTextOnly(HTML_A);
    assertHtmlEquals("Test:AtitleContent", sanitized);
  }

  private void assertHtmlEquals(String expected, String actual){
    assertEquals(expected.replaceAll("\\s+", ""), actual.replaceAll("\\s+", ""));
  }

}
