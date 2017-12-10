package pl.kubiczak.maven.sling.filters.maven.plugin;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class FiltersXmlTest {

  @Test
  public void shouldAddSimpleElement() {
    FiltersXml tested = new FiltersXml();
    tested.addFilter("<test/>");

    String actual = tested.prettyXml();
    String expected = ""
        + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<workspaceFilter version=\"1.0\">\n"
        + "  <test/>\n"
        + "</workspaceFilter>\n";

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void shouldAddCommentForIncorrectElement() {
    FiltersXml tested = new FiltersXml();
    tested.addFilter("incorrect XML");

    String actual = tested.prettyXml();

    assertThat(actual, containsString("<!-- filter could not be parsed -->"));
  }
}
