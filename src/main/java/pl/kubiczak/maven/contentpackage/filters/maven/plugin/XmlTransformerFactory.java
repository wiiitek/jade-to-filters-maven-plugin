package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.apache.maven.plugin.logging.Log;

/**
 * Builds transformer for XML formatter.
 */
class XmlTransformerFactory {

  private static final String EXTERNAL_REFERENCES_DENIED = "";

  private final Log mavenLog;

  private final Map<String, Object> factoryAttributes;

  XmlTransformerFactory(Log mavenLog) {
    this.mavenLog = mavenLog;
    this.factoryAttributes = new LinkedHashMap<String, Object>();
  }

  XmlTransformerFactory addFactoryAttribute(String key, Object value) {
    this.factoryAttributes.put(key, value);
    return this;
  }

  TransformerFactory create()
      throws TransformerConfigurationException {
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, EXTERNAL_REFERENCES_DENIED);
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, EXTERNAL_REFERENCES_DENIED);
    // factory attributes entry set is ordered (as the implementation is LinkedHashMap)
    mavenLog.debug("Setting attributes: '" + factoryAttributes
        + "' for the factory: '" + factory + "'.");
    for (Map.Entry<String, Object> entry : factoryAttributes.entrySet()) {
      try {
        factory.setAttribute(entry.getKey(), entry.getValue());
      } catch (IllegalArgumentException iae) {
        mavenLog.error("Error while setting attribute '"
            + entry.getKey() + ":" + entry.getValue()
            + "' for factory: '" + factory + "'.", iae);
      }
    }
    return factory;
  }
}
