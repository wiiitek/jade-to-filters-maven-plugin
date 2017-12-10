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

  String transformToXml(String filename) throws IOException {
    final String result;
    LOG.debug("transforming a file: '{}'", filename);
    URL templateUrl = getClass().getClassLoader().getResource(filename);
    if (templateUrl == null) {
      LOG.warn("the resource: '{}' could not be found", filename);
      result = "<!-- the resource '" + filename + "' could not be found -->";
    } else {
      result = Jade4J.render(templateUrl, null, true);
    }
    return result;
  }

}