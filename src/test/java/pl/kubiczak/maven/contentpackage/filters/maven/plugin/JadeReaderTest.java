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
public class JadeReaderTest {


  @Mock
  private Log mavenLogMock;

  @Test
  public void transformToXml_shouldTransformSimpleFilter() {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFileUrl = getClass().getClassLoader().getResource("simple-filter.jade");
    String actual = tested.transformToXml(jadeFileUrl);
    String expected = ""
        + "<filters>\n"
        + "<filter root=\"/content/test\"></filter>"
        + "</filters>";

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void transformToXml_shouldReadTwoFilters() {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFileUrl = getClass().getClassLoader().getResource("two-filters.jade");
    String actual = tested.transformToXml(jadeFileUrl);
    String expected = ""
        + "<filters>\n"
        + "<filter root=\"/content/test\"></filter>\n"
        + "<filter root=\"/content/dam/test\"></filter>"
        + "</filters>";

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void transformToXml_shouldReadComments() {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFileUrl = getClass().getClassLoader().getResource("filter-with-comment.jade");
    String actual = tested.transformToXml(jadeFileUrl);
    String expected = ""
        + "<filters>\n"
        + "<filter root=\"/content/project/dam\">\n"
        + "  <!-- let's exclude all renditions for DAM nodes-->\n"
        + "  <exclude pattern=\"/content/project/dam(/.*)?/renditions(/.*)?\"></exclude>\n"
        + "  <!--\n"
        + "  but we want to to include original rendition\n"
        + "  so that others could be regenerated\n"
        + "  -->\n"
        + "  <include pattern=\"/content/project/dam(/.*)?/renditions/original\"></include>\n"
        + "</filter>"
        + "</filters>";

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void transformToXml_shouldThrowExceptionForIncorrectSyntax() {
    JadeReader tested = new JadeReader(mavenLogMock);
    URL jadeFileUrl = getClass().getClassLoader().getResource("incorrect.jade");

    assertThatExceptionOfType(JadeLexerException.class)
        .isThrownBy(() -> {
          tested.transformToXml(jadeFileUrl);
        }).withMessageMatching("The end of the string was reached with no closing bracket found.*");
  }
}
