package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import pl.kubiczak.maven.sling.filters.maven.plugin.jade.Jade;
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

  FiltersXml addFilterFromJadeFile(String filename) throws IOException {
    String xml = new Jade().transformToXml(filename);
    this.addFilter(xml);
    return this;
  }

  FiltersXml addFilter(String filterXml) {

    Document filterDoc = new Parser().parse(filterXml);
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

  String prettyXml() {
    return new Pretty().format(filters);
  }
}
