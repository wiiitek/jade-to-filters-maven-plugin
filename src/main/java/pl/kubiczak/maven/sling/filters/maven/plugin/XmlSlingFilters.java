package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Main class for Sling filters creation.
 */
class XmlSlingFilters {

  private static final Logger LOG = LoggerFactory.getLogger(XmlSlingFilters.class);

  private final Document filters;

  XmlSlingFilters() {
    String xml = "<workspaceFilter version=\"1.0\"/>";
    this.filters = new XmlParser().parse(xml);
    // for formatting - https://stackoverflow.com/a/8438236
    this.filters.setXmlStandalone(true);
  }

  XmlSlingFilters addFilterFromJadeFile(String filename) throws IOException {
    String xml = new JadeReader().transformToXml(filename);
    this.addFilter(xml);
    return this;
  }

  String prettyXml() {
    return new XmlFormatter().format(filters);
  }

  XmlSlingFilters addFilter(String filterXml) {

    Document filterDoc = new XmlParser().parse(filterXml);
    if (filterDoc == null) {
      LOG.warn("XML could not be parsed");
      Comment comment = filters.createComment(" filter could not be parsed ");
      filters.appendChild(comment);
    } else {
      LOG.debug("including '{}' in filters", filterDoc);
      Node copy = filterDoc.getDocumentElement().cloneNode(true);
      filters.adoptNode(copy);
      filters.getDocumentElement().appendChild(copy);
    }
    return this;
  }
}
