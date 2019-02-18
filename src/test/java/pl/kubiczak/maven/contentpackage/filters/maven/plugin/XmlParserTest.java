package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import org.apache.maven.plugin.logging.Log;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;

@RunWith(MockitoJUnitRunner.class)
public class XmlParserTest {

  private static final String XML_WITH_EXTERNAL_ENTITIES = ""
      + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<!DOCTYPE root [<!ENTITY insecure SYSTEM 'file:///etc/passwd'>]><root>&insecure;</root>"
      + "<workspaceFilter version=\"1.0\">\n"
      + "  <test/>\n"
      + "</workspaceFilter>\n";

  @Mock
  private Log mavenLogMock;

  @Test
  @Ignore("This test is only for manual execution to see error for external entites in XML")
  public void parse() {
    new XmlParser(mavenLogMock).parse(XML_WITH_EXTERNAL_ENTITIES);
  }
}