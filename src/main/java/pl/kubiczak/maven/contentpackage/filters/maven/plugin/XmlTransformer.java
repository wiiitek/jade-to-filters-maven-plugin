package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.apache.maven.plugin.logging.Log;

class XmlTransformer {

  private final Log mavenLog;

  private final XmlTransformerFactory xmlTransformerFactory;

  private final Map<String, String> outputProperties;

  XmlTransformer(Log mavenLog, XmlTransformerFactory xmlTransformerFactory) {
    this.mavenLog = mavenLog;
    this.xmlTransformerFactory = xmlTransformerFactory;
    this.outputProperties = new LinkedHashMap<String, String>();
  }

  XmlTransformer addOutputProperty(String key, String value) {
    this.outputProperties.put(key, value);
    return this;
  }

  Transformer create()
      throws TransformerConfigurationException {
    TransformerFactory factory = xmlTransformerFactory.create();
    Transformer transformer;
    try {
      transformer = factory.newTransformer();
    } catch (TransformerConfigurationException tce) {
      mavenLog.error(
          "Error while creating transformer with factory: '" + factory + "'.", tce);
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
