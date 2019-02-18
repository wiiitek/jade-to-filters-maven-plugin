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
    Document document = null;
    try {
      DocumentBuilder docBuilder = createSecureFactory().newDocumentBuilder();
      InputSource in = new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
      document = docBuilder.parse(in);
    } catch (ParserConfigurationException pce) {
      logXmlException("Error while creating XML document builder.", pce, xml);
    } catch (UnsupportedEncodingException uee) {
      logXmlException("UTF-8 is unsupported.", uee, xml);
    } catch (SAXException saxe) {
      logXmlException("Parsing error.", saxe, xml);
    } catch (IOException ioe) {
      logXmlException("Input stream error in DocumentBuilder.", ioe, xml);
    } catch (IllegalArgumentException iae) {
      logXmlException("Error with creating input source.", iae, xml);
    }
    return document;
  }

  private void logXmlException(String msg, Exception ex, String xml) {
    mavenLog.error(msg + " message: '" + ex.getMessage() + "' for XML:\n" + xml, ex);
  }

  /**
   * https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md#jaxp-documentbuilderfactory-saxparserfactory-and-dom4j
   *
   * @return factory secured against external entity attacks
   */
  private DocumentBuilderFactory createSecureFactory() {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    String feature = null;
    try {
      // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all
      // XML entity attacks are prevented
      // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
      feature = "http://apache.org/xml/features/disallow-doctype-decl";
      dbf.setFeature(feature, true);
      // If you can't completely disable DTDs, then at least do the following:
      // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
      // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
      // JDK7+ - http://xml.org/sax/features/external-general-entities
      feature = "http://xml.org/sax/features/external-general-entities";
      dbf.setFeature(feature, false);
      // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
      // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
      // JDK7+ - http://xml.org/sax/features/external-parameter-entities
      feature = "http://xml.org/sax/features/external-parameter-entities";
      dbf.setFeature(feature, false);
      // Disable external DTDs as well
      feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
      dbf.setFeature(feature, false);
      // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
      dbf.setXIncludeAware(false);
      dbf.setExpandEntityReferences(false);
    } catch (ParserConfigurationException e) {
      // This should catch a failed setFeature feature
      mavenLog.info("ParserConfigurationException was thrown. The feature '" + feature
          + "' is probably not supported by your XML processor.");
    }
    return dbf;
  }
}
