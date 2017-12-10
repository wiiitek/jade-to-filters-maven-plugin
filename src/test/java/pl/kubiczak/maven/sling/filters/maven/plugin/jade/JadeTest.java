package pl.kubiczak.maven.sling.filters.maven.plugin.jade;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import org.junit.Test;
import pl.kubiczak.maven.sling.filters.maven.plugin.jade.Jade;

public class JadeTest {

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
}
