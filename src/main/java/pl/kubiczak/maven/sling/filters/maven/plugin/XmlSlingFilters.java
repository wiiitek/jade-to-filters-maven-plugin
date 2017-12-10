package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

  String prettyXml() {
    return new XmlFormatter().format(filters);
  }

  XmlSlingFilters addFromFile(String jadeFile) throws IOException {
    URL jadeFileUrl = new File(jadeFile).toURI().toURL();
    String xml = new JadeReader().transformToXml(jadeFileUrl);
    this.addFromXml(xml);
    return this;
  }

  XmlSlingFilters addFromFile(URL jadeFilterUrl) throws IOException {
    String xml = new JadeReader().transformToXml(jadeFilterUrl);
    this.addFromXml(xml);
    return this;
  }

  XmlSlingFilters addFromXml(String filterXml) {

    Document filterDoc = new XmlParser().parse(filterXml);
    if (filterDoc == null) {
      LOG.warn("XML could not be parsed");
      Comment comment = filters.createComment(" some filters could not be parsed ");
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
