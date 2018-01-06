package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.apache.maven.plugin.logging.Log;

/**
 * Builds transformer for XML formatter.
 */
class XmlTransformerBuilder {

  private final Log mavenLog;

  private final Map<String, Object> factoryAttributes;

  private final Map<String, String> outputProperties;

  XmlTransformerBuilder(Log mavenLog) {
    this.mavenLog = mavenLog;
    this.factoryAttributes = new LinkedHashMap<String, Object>();
    this.outputProperties = new LinkedHashMap<String, String>();
  }

  XmlTransformerBuilder addOutputProperty(String key, String value) {
    this.outputProperties.put(key, value);
    return this;
  }

  XmlTransformerBuilder addFactoryAttribute(String key, Object value) {
    this.factoryAttributes.put(key, value);
    return this;
  }

  Transformer build() throws TransformerConfigurationException {
    TransformerFactory factory = buildFactory();
    return buildTransformer(factory);
  }

  private TransformerFactory buildFactory() {
    TransformerFactory factory = TransformerFactory.newInstance();
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

  private Transformer buildTransformer(TransformerFactory factory)
      throws TransformerConfigurationException {
    Transformer transformer;
    try {
      transformer = factory.newTransformer();
    } catch (TransformerConfigurationException tce) {
      mavenLog.error("Error while creating transformer with factory: '" + factory
          + "' and factory attributes: '" + factoryAttributes + "'.", tce);
      throw tce;
    }
    mavenLog.debug("Setting output properties: '" + outputProperties
        + "' for the transformer: '" + transformer + "'.");
    for (Map.Entry<String, String> entry : outputProperties.entrySet()) {
      try {
        transformer.setOutputProperty(entry.getKey(), entry.getValue());
      } catch (IllegalArgumentException iae) {
        mavenLog.error("Error while setting property '"
            + entry.getKey() + ":" + entry.getValue()
            + "' for transformer: '" + transformer + "'.");
      }
    }
    return transformer;
  }
}
