package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class XmlTransformerBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(XmlTransformerBuilder.class);

  private final Map<String, Object> factoryAttributes;

  private final Map<String, String> outputProperties;

  XmlTransformerBuilder() {
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
    LOG.debug("setting attributes: '{}' for the factory: '{}'",
        factoryAttributes, factory);
    for (Map.Entry<String, Object> entry : factoryAttributes.entrySet()) {
      try {
        factory.setAttribute(entry.getKey(), entry.getValue());
      } catch (IllegalArgumentException iae) {
        LOG.error("error while setting attribute '{}:{}' for factory: '{}'",
            entry.getKey(), entry.getValue(), factory);
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
      LOG.error("error while creating transformer with factory: '{}' and factory attributes: '{}'",
          factory, factoryAttributes, tce);
      throw tce;
    }
    LOG.debug("setting output properties: '{}' for the transformer: '{}'",
        outputProperties, transformer);
    for (Map.Entry<String, String> entry : outputProperties.entrySet()) {
      try {
        transformer.setOutputProperty(entry.getKey(), entry.getValue());
      } catch (IllegalArgumentException iae) {
        LOG.error("error while setting property '{}:{}' for transformer: '{}'",
            entry.getKey(), entry.getValue(), transformer);
      }
    }
    return transformer;
  }
}
