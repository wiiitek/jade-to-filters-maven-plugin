package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Parse XML text.
 */
class XmlParser {

  private final Log mavenLog;

  XmlParser(Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  Document parse(String xml) {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    Document document = null;
    try {
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      InputSource in = new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
      document = docBuilder.parse(in);
    } catch (SAXParseException spe) {
      mavenLog.error("error: '" + spe.getMessage() + "'. "
          + "Incorrect XML being parsed into XML document:\n" + xml, spe);
    } catch (ParserConfigurationException pce) {
      mavenLog.error("error while creating XML document builder", pce);
    } catch (SAXException saxe) {
      mavenLog.error("error: '" + saxe.getMessage() + "' while parsing XML:\n" + xml, saxe);
    } catch (UnsupportedEncodingException uee) {
      mavenLog.error("Unsupported encoding: UTF-8");
    } catch (IOException ioe) {
      mavenLog.error("error: '" + ioe.getMessage() + "' while parsing XML:\n" + xml, ioe);
    }
    return document;
  }
}
