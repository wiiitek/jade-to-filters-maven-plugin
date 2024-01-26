package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.apache.maven.plugin.logging.Log;

class FiltersCreatorXmlFormatterTransformer {

  private final Log mavenLog;

  private final TransformerFactoryBuilder transformerFactoryBuilder;

  private final Map<String, String> outputProperties;

  FiltersCreatorXmlFormatterTransformer(
      Log mavenLog,
      TransformerFactoryBuilder transformerFactoryBuilder
  ) {
    this.mavenLog = mavenLog;
    this.transformerFactoryBuilder = transformerFactoryBuilder;
    this.outputProperties = new LinkedHashMap<>();
  }

  FiltersCreatorXmlFormatterTransformer addOutputProperty(String key, String value) {
    this.outputProperties.put(key, value);
    return this;
  }

  Transformer create()
      throws TransformerConfigurationException {
    TransformerFactory factory = transformerFactoryBuilder.create();
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
