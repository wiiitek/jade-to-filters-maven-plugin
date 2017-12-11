package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Main class for Sling filters creation.
 */
class XmlSlingFilters {

  private final Log mavenLog;

  private final Document filters;

  private static final boolean DEEP = true;

  XmlSlingFilters(Log mavenLog) {
    this.mavenLog = mavenLog;
    String xml = "<workspaceFilter version=\"1.0\"/>";
    this.filters = new XmlParser(mavenLog).parse(xml);
    // for formatting - https://stackoverflow.com/a/8438236
    this.filters.setXmlStandalone(true);
  }

  String prettyXml() {
    mavenLog.debug("formatting Sling filters XML...");
    return new XmlFormatter(mavenLog).format(filters);
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

  XmlSlingFilters addFromFile(URL jadeFileUrl) {
    String xml = new JadeReader(mavenLog).transformToXml(jadeFileUrl);
    this.addFromXml(xml);
    return this;
  }

  XmlSlingFilters addFromXml(String filterXml) {

    Document filterDoc = new XmlParser(mavenLog).parse(filterXml);
    if (filterDoc == null) {
      mavenLog.warn("XML could not be parsed");
      Comment comment = filters.createComment(" some filters could not be parsed ");
      filters.appendChild(comment);
    } else {
      mavenLog.debug("adding filters from XML:\n" + filterXml);
      NodeList filtersToAdd = filterDoc.getDocumentElement().getChildNodes();
      if (filtersToAdd.getLength() == 0) {
        mavenLog.warn("no filters to add from XML:\n" + filterXml);
      } else {
        for (int i = 0; i < filtersToAdd.getLength(); i++) {
          Node copy = filtersToAdd.item(i).cloneNode(DEEP);
          filters.adoptNode(copy);
          filters.getDocumentElement().appendChild(copy);
        }
      }
    }
    return this;
  }
}
