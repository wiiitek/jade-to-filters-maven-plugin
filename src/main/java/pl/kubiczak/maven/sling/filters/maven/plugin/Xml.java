package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Manipulations on XML file for filters.
 */
class Xml {

  private static final Logger LOG = LoggerFactory.getLogger(Xml.class);

  Document emptyFilters() {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    Document document = null;
    //String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><workspaceFilter vesion=\"1.0\"/>";
    String xml = "<workspaceFilter version=\"1.0\"/>";

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
}
