package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Manipulations on XML file for filters.
 */
class FiltersXml {

  private static final Logger LOG = LoggerFactory.getLogger(FiltersXml.class);

  private static final String LONG = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

  private static final String SHORT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

  private final Document filters;

  FiltersXml() {
    String xml = "<workspaceFilter version=\"1.0\"/>";
    this.filters = parse(xml);
  }

  void addFilter(String filterXml) {
    Document filterDoc = parse(filterXml);
    Node copy = filterDoc.getDocumentElement().cloneNode(true);

    filters.adoptNode(copy);
    filters.getDocumentElement().appendChild(copy);
  }

  private Document parse(String xml) {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    Document document = null;
    try {
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      InputSource in = new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
      document = docBuilder.parse(in);
    } catch (ParserConfigurationException pce) {
      LOG.error("error while creating XML document builder", pce);
    } catch (SAXException saxe) {
      LOG.error("error while parsinx XML: {}:\n{}", saxe.getMessage(), xml, saxe);
    } catch (UnsupportedEncodingException uee) {
      LOG.error("Unsupported encoding: UTF-8");
    } catch (IOException ioe) {
      LOG.error("error while parsinx XML: {}:\n{}", ioe.getMessage(), xml, ioe);
    }
    return document;
  }

  @Override
  public String toString() {
    String xml = null;
    try {
      DOMSource domSource = new DOMSource(filters);
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      createTransformer().transform(domSource, result);
      writer.flush();
      xml = writer.toString();
    } catch (TransformerConfigurationException tce) {
      LOG.error("error while creating new transformer", tce);
    } catch (TransformerException te) {
      LOG.error("error while transforming XML document", te);
    }
    xml = xml.replace("\r\n", "\n");
    return xml.replace(LONG, SHORT);
  }

  private Transformer createTransformer() {
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setAttribute("indent-number", new Integer(2));
    Transformer transformer = null;
    try {
      transformer = tf.newTransformer();
    } catch (TransformerConfigurationException tce) {
      LOG.error("error while creating transformer", tce);
    }

    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

    return transformer;
  }
}
