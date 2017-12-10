package pl.kubiczak.maven.sling.filters.maven.plugin.xml;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import org.junit.Test;

public class FiltersTest {

  private static final String SIMPLE_XML = ""
      + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<workspaceFilter version=\"1.0\">\n"
      + "  <test/>\n"
      + "</workspaceFilter>\n";

  @Test
  public void addFilter_shouldAddSimpleElement() {
    String actual = new Filters().addFilter("<test></test>").prettyXml();

    assertThat(actual, equalTo(SIMPLE_XML));
  }

  @Test
  public void addFilter_shouldAddCommentForIncorrectElement() {
    String actual = new Filters().addFilter("incorrect XML").prettyXml();

    assertThat(actual, containsString("<!-- filter could not be parsed -->"));
  }

  @Test
  public void addFilterFromJadeFile_shouldAddElementFromFile() throws IOException {
    String actual = new Filters().addFilterFromJadeFile("./test.jade").prettyXml();

    assertThat(actual, equalTo(SIMPLE_XML));
  }

}
