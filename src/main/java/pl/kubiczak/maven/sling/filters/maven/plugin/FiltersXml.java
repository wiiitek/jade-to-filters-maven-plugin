package pl.kubiczak.maven.sling.filters.maven.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import pl.kubiczak.maven.sling.filters.maven.plugin.xml.Parser;
import pl.kubiczak.maven.sling.filters.maven.plugin.xml.Pretty;

/**
 * Manipulations on XML file for filters.
 */
class FiltersXml {

  private static final Logger LOG = LoggerFactory.getLogger(FiltersXml.class);

  private final Document filters;

  FiltersXml() {
    String xml = "<workspaceFilter version=\"1.0\"/>";
    this.filters = new Parser().parse(xml);
    // for formatting - https://stackoverflow.com/a/8438236
    this.filters.setXmlStandalone(true);
  }

  void addFilter(String filterXml) {
    Document filterDoc = new Parser().parse(filterXml);
    Node copy = filterDoc.getDocumentElement().cloneNode(true);

    filters.adoptNode(copy);
    filters.getDocumentElement().appendChild(copy);
  }

  String prettyXml() {
    return new Pretty().format(filters);
  }
}
