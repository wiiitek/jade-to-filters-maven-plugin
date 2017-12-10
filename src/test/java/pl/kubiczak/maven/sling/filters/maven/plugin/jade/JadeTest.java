package pl.kubiczak.maven.sling.filters.maven.plugin.jade;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import de.neuland.jade4j.exceptions.JadeLexerException;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JadeTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void test() throws IOException {
    Jade tested = new Jade();

    String actual = tested.transformToXml("./sample.jade");
    String expected = "\n"
        + "<workspaceFilter version=\"1.0\">\n"
        + "  <filter root=\"/content/test\"></filter>\n"
        + "</workspaceFilter>";

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void shouldThrowExceptionForIncorrectSyntax() throws IOException {
    Jade tested = new Jade();
    exception.expect(JadeLexerException.class);
    tested.transformToXml("./incorrect.jade");
  }
}
