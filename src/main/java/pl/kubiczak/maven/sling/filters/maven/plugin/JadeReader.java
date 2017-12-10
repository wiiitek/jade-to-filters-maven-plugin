package pl.kubiczak.maven.sling.filters.maven.plugin;

import de.neuland.jade4j.Jade4J;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms Jade file into XML string.
 */
class JadeReader {

  private static final Logger LOG = LoggerFactory.getLogger(JadeReader.class);

  String transformToXml(URL jadeFilterUrl) throws IOException {
    final String result;
    LOG.debug("transforming a template with URL: '{}'", jadeFilterUrl);
    return Jade4J.render(jadeFilterUrl, null, true);
  }

}
