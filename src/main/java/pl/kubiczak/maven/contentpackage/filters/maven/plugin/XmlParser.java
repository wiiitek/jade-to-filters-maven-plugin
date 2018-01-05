package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

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
    } catch (ParserConfigurationException pce) {
      logXmlException("error while creating XML document builder", pce, xml);
    } catch (UnsupportedEncodingException uee) {
      logXmlException("UTF-8 is unsupported.", uee, xml);
    } catch (SAXException saxe) {
      logXmlException("parsing error.", saxe, xml);
    } catch (IOException ioe) {
      logXmlException("input stream error in DocumentBuilder.", ioe, xml);
    } catch (IllegalArgumentException iae) {
      logXmlException("error with creating input source", iae, xml);
    }
    return document;
  }

  private void logXmlException(String msg, Exception ex, String xml) {
    mavenLog.error(msg + " message: '" + ex.getMessage() + "' for XML:\n" + xml, ex);
  }
}
