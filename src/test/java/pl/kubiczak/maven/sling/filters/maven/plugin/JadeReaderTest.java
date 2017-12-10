package pl.kubiczak.maven.sling.filters.maven.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import de.neuland.jade4j.exceptions.JadeLexerException;
import java.io.IOException;
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
  public void transformToXml_shouldTransformSimpleFilter() throws IOException {
    JadeReader tested = new JadeReader(mavenLogMock);

    URL jadeFilterUrl = getClass().getClassLoader().getResource("simple-filter.jade");
    String actual = tested.transformToXml(jadeFilterUrl);
    String expected = "\n"
        + "<workspaceFilter version=\"1.0\">\n"
        + "  <filter root=\"/content/test\"></filter>\n"
        + "</workspaceFilter>";

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void transformToXml_shouldThrowExceptionForIncorrectSyntax() throws IOException {
    JadeReader tested = new JadeReader(mavenLogMock);
    URL jadeFilterUrl = getClass().getClassLoader().getResource("incorrect.jade");
    exception.expect(JadeLexerException.class);
    tested.transformToXml(jadeFilterUrl);
  }
}
