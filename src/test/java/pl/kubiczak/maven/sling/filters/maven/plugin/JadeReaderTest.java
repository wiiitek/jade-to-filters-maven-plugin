package pl.kubiczak.maven.sling.filters.maven.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import de.neuland.jade4j.exceptions.JadeLexerException;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pl.kubiczak.maven.sling.filters.maven.plugin.JadeReader;

public class JadeReaderTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void transformToXml_shouldTransformSimpleFilter() throws IOException {
    JadeReader tested = new JadeReader();

    String actual = tested.transformToXml("./simple-filter.jade");
    String expected = "\n"
        + "<workspaceFilter version=\"1.0\">\n"
        + "  <filter root=\"/content/test\"></filter>\n"
        + "</workspaceFilter>";

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void transformToXml_shouldThrowExceptionForIncorrectSyntax() throws IOException {
    JadeReader tested = new JadeReader();
    exception.expect(JadeLexerException.class);
    tested.transformToXml("./incorrect.jade");
  }
}
