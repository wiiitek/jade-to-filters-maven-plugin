package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Main class for filters creation.
 */
class FiltersCreator {

  private final Log mavenLog;

  private final Document filters;

  private static final boolean DEEP = true;

  FiltersCreator(Log mavenLog) {
    this.mavenLog = mavenLog;
    String xml = "<workspaceFilter version=\"1.0\"/>";
    this.filters = new XmlParser(mavenLog).parse(xml);
    // for formatting - https://stackoverflow.com/a/8438236
    this.filters.setXmlStandalone(true);
  }

  String prettyXml() {
    mavenLog.debug("Formatting filters XML...");
    return new FiltersCreatorXmlFormatter(mavenLog).format(filters);
  }

  FiltersCreator addFromFile(String jadeFilename) {
    File jadeFile = new File(jadeFilename);
    if (!jadeFile.exists()) {
      mavenLog.error("The input file '" + jadeFilename + "' does not exists!");
    } else {
      if (!jadeFile.isFile()) {
        mavenLog.error("The input file '" + jadeFilename + "' exist but is not a file!");
      } else {
        URL jadeFileUrl = fromFile(jadeFile, jadeFilename);
        this.addFromFile(jadeFileUrl);
      }
    }
    return this;
  }

  FiltersCreator addFromFile(URL jadeFileUrl) {
    String xml = new JadeReader(mavenLog).transformToXml(jadeFileUrl);
    if (xml != null) {
      this.addFromXml(xml);
    }
    return this;
  }

  FiltersCreator addFromXml(String filterXml) {

    Document filterDoc = new XmlParser(mavenLog).parse(filterXml);
    if (filterDoc == null) {
      mavenLog.warn("XML could not be parsed");
      Comment comment = filters.createComment(" some filters could not be parsed ");
      filters.appendChild(comment);
    } else {
      mavenLog.debug("Adding filters from XML:\n" + filterXml);
      NodeList filtersToAdd = filterDoc.getDocumentElement().getChildNodes();
      if (filtersToAdd.getLength() == 0) {
        mavenLog.warn("No filters to add from XML:\n" + filterXml);
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

  private URL fromFile(File file, String filename) {
    URL result = null;
    try {
      result = file.toURI().toURL();
    } catch (MalformedURLException e) {
      mavenLog.error("Error while getting URL for '" + filename + "'.");
    }
    return result;
  }
}
