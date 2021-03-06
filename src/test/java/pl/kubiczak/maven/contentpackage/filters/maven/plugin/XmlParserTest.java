package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@RunWith(MockitoJUnitRunner.class)
public class XmlParserTest {

  private static final String XML_WITH_EXTERNAL_ENTITIES = ""
      + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<!DOCTYPE root [<!ENTITY insecure SYSTEM 'file:///etc/passwd'>]><root>&insecure;</root>"
      + "<workspaceFilter version=\"1.0\">\n"
      + "  <value>&insecure;</value>\n"
      + "</workspaceFilter>\n";

  private static final String XML = ""
      + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<zażółć version=\"1.0\">\n"
      + "  <test/>\n"
      + "</zażółć>\n";

  @Mock
  private Log mavenLogMock;

  @Test
  public void shouldNotParseInsecureXml() {
    Document parsed = new XmlParser(mavenLogMock).parse(XML_WITH_EXTERNAL_ENTITIES);
    assertThat(parsed, nullValue());
  }

  @Test
  public void shouldParseXml() {
    Document actual = new XmlParser(mavenLogMock).parse(XML);

    Element rootElement = actual.getDocumentElement();
    assertThat(rootElement.getTagName(), equalTo("zażółć"));
    assertThat(rootElement.getAttribute("version"), equalTo("1.0"));
  }
}