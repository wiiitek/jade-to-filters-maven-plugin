package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;
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

  private final Log mavenLog;

  private final Document filters;

  XmlSlingFilters(Log mavenLog) {
    this.mavenLog = mavenLog;
    String xml = "<workspaceFilter version=\"1.0\"/>";
    this.filters = new XmlParser().parse(xml);
    // for formatting - https://stackoverflow.com/a/8438236
    this.filters.setXmlStandalone(true);
  }

  String prettyXml() {
    mavenLog.debug("formatting Sling filters XML...");
    return new XmlFormatter().format(filters);
  }

  XmlSlingFilters addFromFile(String jadeFilename) {
    File jadeFile = new File(jadeFilename);
    if (!jadeFile.exists()) {
      mavenLog.error("the input file '" + jadeFilename + "' does not exists");
    } else {
      if (!jadeFile.isFile()) {
        mavenLog.error("the input file '" + jadeFilename + "' exist but is not a file");
      } else {
        URL jadeFileUrl = null;
        try {
          jadeFileUrl = jadeFile.toURI().toURL();
        } catch (MalformedURLException e) {
          mavenLog.error("error while getting URL for '" + jadeFilename + "'");
        }
        String xml = new JadeReader(mavenLog).transformToXml(jadeFileUrl);
        if (xml != null) {
          this.addFromXml(xml);
        }
      }
    }
    return this;
  }

  XmlSlingFilters addFromFile(URL jadeFilterUrl) throws IOException {
    String xml = new JadeReader(mavenLog).transformToXml(jadeFilterUrl);
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
