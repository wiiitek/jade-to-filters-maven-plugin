package pl.kubiczak.maven.sling.filters.maven.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.w3c.dom.Document;

public class XmlTest {

  @Test
  public void shouldGenerateEmptyFiltersXml() {
    Xml tested = new Xml();

    Document actual = tested.emptyFilters();

    String tag = actual.getDocumentElement().getTagName();
    String version = actual.getDocumentElement().getAttribute("version");

    assertThat(tag, equalTo("workspaceFilter"));
    assertThat(version, equalTo("1.0"));
  }
}
