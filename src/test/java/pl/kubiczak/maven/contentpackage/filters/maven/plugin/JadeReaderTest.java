package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.neuland.jade4j.exceptions.JadeLexerException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JadeReaderTest {

  @Mock
  private Log mavenLogMock;

  @Test
  void transformToXml_shouldTransformSimpleFilter() {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFileUrl = getClass().getClassLoader().getResource("simple-filter.jade");
    String actual = tested.transformToXml(jadeFileUrl);
    String expected =
        """
        <filters>
        <filter root="/content/test"></filter></filters>
        """;

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void transformToXml_shouldReadTwoFilters() {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFileUrl = getClass().getClassLoader().getResource("two-filters.jade");
    String actual = tested.transformToXml(jadeFileUrl);
    String expected =
        """
        <filters>
        <filter root="/content/test"></filter>
        <filter root="/content/dam/test"></filter></filters>
        """;

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void transformToXml_shouldReadComments() {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFileUrl = getClass().getClassLoader().getResource("filter-with-comment.jade");
    String actual = tested.transformToXml(jadeFileUrl);
    String expected =
        """
        <filters>
        <filter root="/content/project/dam">
          <!-- let's exclude all renditions for DAM nodes-->
          <exclude pattern="/content/project/dam(/.*)?/renditions(/.*)?"></exclude>
          <!--
          but we want to to include original rendition
          so that others could be regenerated
          -->
          <include pattern="/content/project/dam(/.*)?/renditions/original"></include>
        </filter></filters>
        """;

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void transformToXml_shouldThrowExceptionForIncorrectSyntax() {
    JadeReader tested = new JadeReader(mavenLogMock);
    URL jadeFileUrl = getClass().getClassLoader().getResource("incorrect.jade");

    assertThatExceptionOfType(JadeLexerException.class)
        .isThrownBy(() -> {
          tested.transformToXml(jadeFileUrl);
        }).withMessageMatching("The end of the string was reached with no closing bracket found.*");
  }
}
