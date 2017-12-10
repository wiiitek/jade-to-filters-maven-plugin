package pl.kubiczak.maven.sling.filters.maven.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import de.neuland.jade4j.exceptions.JadeLexerException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JadeReaderTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Mock
  private Log mavenLogMock;

  @Test
  public void transformToXml_shouldTransformSimpleFilter() {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFilterUrl = getClass().getClassLoader().getResource("simple-filter.jade");
    String actual = tested.transformToXml(jadeFilterUrl);
    String expected = ""
        + "<filters>\n"
        + "<filter root=\"/content/test\"></filter>"
        + "</filters>";

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void transformToXml_shouldReadTwoFilters() {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFilterUrl = getClass().getClassLoader().getResource("two-filters.jade");
    String actual = tested.transformToXml(jadeFilterUrl);
    String expected = ""
        + "<filters>\n"
        + "<filter root=\"/content/test\"></filter>\n"
        + "<filter root=\"/content/dam/test\"></filter>"
        + "</filters>";

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void transformToXml_shouldReadComments() {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFilterUrl = getClass().getClassLoader().getResource("filter-with-comment.jade");
    String actual = tested.transformToXml(jadeFilterUrl);
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

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void transformToXml_shouldThrowExceptionForIncorrectSyntax() {
    JadeReader tested = new JadeReader(mavenLogMock);
    URL jadeFilterUrl = getClass().getClassLoader().getResource("incorrect.jade");
    exception.expect(JadeLexerException.class);
    tested.transformToXml(jadeFilterUrl);
  }
}
