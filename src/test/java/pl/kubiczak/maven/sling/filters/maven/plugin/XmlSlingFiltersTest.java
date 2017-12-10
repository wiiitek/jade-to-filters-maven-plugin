package pl.kubiczak.maven.sling.filters.maven.plugin;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class XmlSlingFiltersTest {

  private static final String SIMPLE_XML = ""
      + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<workspaceFilter version=\"1.0\">\n"
      + "  <test/>\n"
      + "</workspaceFilter>\n";

  private static final String XML = ""
      + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<workspaceFilter version=\"1.0\">\n"
      + "  <filter root=\"/content/test\">\n"
      + "    <exclude pattern=\"/content/test(/.*)?/qa(/.*)?\"/>\n"
      + "  </filter>\n"
      + "  <filter root=\"/content/dam/test\">\n"
      + "    <exclude pattern=\"/content/dam/test(/.*)?/renditions(/.*)?\"/>\n"
      + "    <include pattern=\"/content/dam/test(/.*)?/renditions/original\"/>\n"
      + "    <exclude pattern=\"/content/dam/test(/.*)?/qa(/.*)?\"/>\n"
      + "  </filter>\n"
      + "</workspaceFilter>\n";

  @Mock
  private Log mavenLogMock;

  @Test
  public void addFromXml_shouldAddSimpleElement() {
    String actual = new XmlSlingFilters(mavenLogMock)
        .addFromXml("<test></test>")
        .prettyXml();
    assertThat(actual, equalTo(SIMPLE_XML));
  }

  @Test
  public void addFromXml_shouldAddNestedElement() {
    String actual = new XmlSlingFilters(mavenLogMock)
        .addFromXml("<test><sub-test/></test>")
        .prettyXml();
    assertThat(actual, containsString("  <test>\n    <sub-test/>\n  </test>\n"));
  }

  @Test
  public void addFromXml_shouldAddCommentForIncorrectElement() {
    String actual = new XmlSlingFilters(mavenLogMock)
        .addFromXml("incorrect XML")
        .prettyXml();
    assertThat(actual, containsString("<!-- some filters could not be parsed -->"));
  }

  @Test
  public void addFromFile_shouldAddElementFromUrl() throws IOException {
    URL jadeFilterUrl = getClass().getClassLoader().getResource("test.jade");

    String actual = new XmlSlingFilters(mavenLogMock)
        .addFromFile(jadeFilterUrl)
        .prettyXml();
    assertThat(actual, equalTo(SIMPLE_XML));
  }

  @Test
  public void addFromFile_shouldAddElementFromFilename() throws IOException {
    String filePath = getClass().getClassLoader().getResource("test.jade").getPath();

    String actual = new XmlSlingFilters(mavenLogMock)
        .addFromFile(filePath)
        .prettyXml();
    assertThat(actual, equalTo(SIMPLE_XML));
  }

  @Test
  public void addFromFile_shouldAddAdvancedFilterFromFilename() throws IOException {
    String filter = getClass().getClassLoader().getResource("test-filter.jade").getPath();
    String damFilter = getClass().getClassLoader().getResource("test-dam-filter.jade").getPath();

    String actual = new XmlSlingFilters(mavenLogMock)
        .addFromFile(filter)
        .addFromFile(damFilter)
        .prettyXml();
    assertThat(actual, equalTo(XML));
  }
}
