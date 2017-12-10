package pl.kubiczak.maven.sling.filters.maven.plugin;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.net.URL;
import org.junit.Test;

public class XmlSlingFiltersTest {

  private static final String SIMPLE_XML = ""
      + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<workspaceFilter version=\"1.0\">\n"
      + "  <test/>\n"
      + "</workspaceFilter>\n";

  @Test
  public void addFromXml_shouldAddSimpleElement() {
    String actual = new XmlSlingFilters().addFromXml("<test></test>").prettyXml();
    assertThat(actual, equalTo(SIMPLE_XML));
  }

  @Test
  public void addFromXml_shouldAddNestedElement() {
    String actual = new XmlSlingFilters().addFromXml("<test><sub-test/></test>").prettyXml();
    assertThat(actual, containsString("  <test>\n    <sub-test/>\n  </test>\n"));
  }

  @Test
  public void addFromXml_shouldAddCommentForIncorrectElement() {
    String actual = new XmlSlingFilters().addFromXml("incorrect XML").prettyXml();
    assertThat(actual, containsString("<!-- some filters could not be parsed -->"));
  }

  @Test
  public void addFromFile_shouldAddElementFromUrl() throws IOException {
    URL jadeFilterUrl = getClass().getClassLoader().getResource("test.jade");
    String actual = new XmlSlingFilters().addFromFile(jadeFilterUrl).prettyXml();
    assertThat(actual, equalTo(SIMPLE_XML));
  }

  @Test
  public void addFromFile_shouldAddElementFromFilename() throws IOException {
    String filePath = getClass().getClassLoader().getResource("test.jade").getPath();
    String actual = new XmlSlingFilters().addFromFile(filePath).prettyXml();
    assertThat(actual, equalTo(SIMPLE_XML));
  }
}
