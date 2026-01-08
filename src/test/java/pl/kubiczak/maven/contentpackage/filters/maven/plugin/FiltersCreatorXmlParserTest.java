package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@ExtendWith(MockitoExtension.class)
class FiltersCreatorXmlParserTest {

  @SuppressWarnings("LineLengthCheck")
  private static final String XML_WITH_EXTERNAL_ENTITIES =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <!DOCTYPE root [<!ENTITY insecure SYSTEM 'file:///etc/passwd'>]><root>&insecure;</root><workspaceFilter version="1.0">
        <value>&insecure;</value>
      </workspaceFilter>
      """;

  private static final String XML =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <zażółć version="1.0">
        <test/>
      </zażółć>
      """;

  @Mock
  private Log mavenLogMock;

  @Test
  void shouldNotParseInsecureXml() {
    Document parsed = new FiltersCreatorXmlParser(mavenLogMock).parse(XML_WITH_EXTERNAL_ENTITIES);
    assertThat(parsed).isNull();
  }

  @Test
  void shouldParseXml() {
    Document actual = new FiltersCreatorXmlParser(mavenLogMock).parse(XML);

    Element rootElement = actual.getDocumentElement();
    assertThat(rootElement.getTagName()).isEqualTo("zażółć");
    assertThat(rootElement.getAttribute("version")).isEqualTo("1.0");
  }
}
