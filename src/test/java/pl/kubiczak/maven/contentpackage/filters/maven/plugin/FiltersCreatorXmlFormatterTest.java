package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URL;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FiltersCreatorXmlFormatterTest {

  private static final String SIMPLE_XML = """
      <?xml version="1.0" encoding="UTF-8"?>
      <workspaceFilter version="1.0">
        <test/>
      </workspaceFilter>
      """;

  private static final String XML = """
      <?xml version="1.0" encoding="UTF-8"?>
      <workspaceFilter version="1.0">
        <filter root="/content/test">
          <exclude pattern="/content/test(/.*)?/qa(/.*)?"/>
        </filter>
        <filter root="/content/dam/test">
          <exclude pattern="/content/dam/test(/.*)?/renditions(/.*)?"/>
          <include pattern="/content/dam/test(/.*)?/renditions/original"/>
          <exclude pattern="/content/dam/test(/.*)?/qa(/.*)?"/>
        </filter>
      </workspaceFilter>
      """;

  @Mock
  private Log mavenLogMock;

  @Test
  void addFromXml_shouldAddSimpleElement() {
    String actual = new FiltersCreator(mavenLogMock)
        .addFromXml("<filters><test></test></filters>")
        .prettyXml();
    assertThat(actual).isEqualTo(SIMPLE_XML);
  }

  @Test
  void addFromXml_shouldAddNestedElement() {
    String actual = new FiltersCreator(mavenLogMock)
        .addFromXml("<filters><test><sub-test/></test></filters>")
        .prettyXml();
    assertThat(actual).contains("  <test>\n    <sub-test/>\n  </test>\n");
  }

  @Test
  void addFromXml_shouldAddCommentForIncorrectElement() {
    String actual = new FiltersCreator(mavenLogMock)
        .addFromXml("<filters>incorrect < XML</filters>")
        .prettyXml();
    assertThat(actual).contains("<!-- some filters could not be parsed -->");
  }

  @Test
  void addFromFile_shouldAddElementFromUrl() {
    URL jadeFileUrl = getClass().getClassLoader().getResource("test.jade");

    String actual = new FiltersCreator(mavenLogMock)
        .addFromFile(jadeFileUrl)
        .prettyXml();
    assertThat(actual).isEqualTo(SIMPLE_XML);
  }

  @Test
  void addFromFile_shouldAddElementFromFilename() {
    String filePath = getClass().getClassLoader().getResource("test.jade").getPath();

    String actual = new FiltersCreator(mavenLogMock)
        .addFromFile(filePath)
        .prettyXml();
    assertThat(actual).isEqualTo(SIMPLE_XML);
  }

  @Test
  void addFromFile_shouldAddFiltersFromTwoFiles() {
    String filterJadeFile =
        getClass().getClassLoader().getResource("test-filter.jade").getPath();
    String damFilterJadeFile =
        getClass().getClassLoader().getResource("test-dam-filter.jade").getPath();

    String actual = new FiltersCreator(mavenLogMock)
        .addFromFile(filterJadeFile)
        .addFromFile(damFilterJadeFile)
        .prettyXml();
    assertThat(actual).isEqualTo(XML);
  }

  @Test
  void addFromFile_shouldLogErrorForNonExistingFile() {
    String filterJadeFile =
        getClass().getClassLoader().getResource("test-filter.jade").getPath();
    String nonExistingFile =
        filterJadeFile + ".non-exisiting";
    String damFilterJadeFile =
        getClass().getClassLoader().getResource("test-dam-filter.jade").getPath();
    String actual = new FiltersCreator(mavenLogMock)
        .addFromFile(filterJadeFile)
        .addFromFile(nonExistingFile)
        .addFromFile(damFilterJadeFile)
        .prettyXml();

    assertThat(actual).isEqualTo(XML);
    String expectedMessage = "The input file '" + nonExistingFile + "' does not exists!";
    verify(mavenLogMock, times(1)).error(expectedMessage);
  }
}
