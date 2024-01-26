package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.apache.maven.plugin.logging.Log;

class TransformerBuilder {

  private final Log mavenLog;

  private final TransformerFactory transformerFactory;

  private final Map<String, String> outputProperties;

  TransformerBuilder(
      Log mavenLog,
      TransformerFactory transformerFactory
  ) {
    this.mavenLog = mavenLog;
    this.transformerFactory = transformerFactory;
    this.outputProperties = new LinkedHashMap<>();
  }

  TransformerBuilder addOutputProperty(String key, String value) {
    this.outputProperties.put(key, value);
    return this;
  }

  Transformer create()
      throws TransformerConfigurationException {
    Transformer transformer;
    try {
      transformer = transformerFactory.newTransformer();
    } catch (TransformerConfigurationException tce) {
      mavenLog.error(
          "Error while creating transformer with factory: '" + transformerFactory + "'.", tce);
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
