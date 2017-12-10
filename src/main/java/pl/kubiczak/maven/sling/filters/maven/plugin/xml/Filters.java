package pl.kubiczak.maven.sling.filters.maven.plugin.xml;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import pl.kubiczak.maven.sling.filters.maven.plugin.jade.Jade;

/**
 * Manipulations on XML file for filters.
 */
public class Filters {

  private static final Logger LOG = LoggerFactory.getLogger(Filters.class);

  private final Document filters;

  /**
   * Creates a class for Sling filters creation.
   */
  public Filters() {
    String xml = "<workspaceFilter version=\"1.0\"/>";
    this.filters = new Parser().parse(xml);
    // for formatting - https://stackoverflow.com/a/8438236
    this.filters.setXmlStandalone(true);
  }

  /*
   * Adds filter definition from Jade file
   *
   * @param filename path to Jade file
   *
   */

  /**
   * Adds filter definition from Jade file.
   *
   * @param filename path to Jade file
   * @return current instance
   * @throws IOException for errors with reading or parsing Jade file or with XML operations
   */
  public Filters addFilterFromJadeFile(String filename) throws IOException {
    String xml = new Jade().transformToXml(filename);
    this.addFilter(xml);
    return this;
  }

  /**
   * Formats XML with indentation and linux newlines.
   *
   * @return formatted XML for Sling filters file
   */
  public String prettyXml() {
    return new Pretty().format(filters);
  }

  Filters addFilter(String filterXml) {

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
}
