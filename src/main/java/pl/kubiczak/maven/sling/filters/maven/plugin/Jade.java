package pl.kubiczak.maven.sling.filters.maven.plugin;

import de.neuland.jade4j.Jade4J;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms a YAML file into XML.
 */
class Jade {

  private static final Logger LOG = LoggerFactory.getLogger(Jade.class);

  String transformToXml(String filename) throws IOException {
    LOG.debug("transforming a file: '{}'", filename);
    URL templateUrl = getClass().getClassLoader().getResource(filename);
    return Jade4J.render(templateUrl, null, true);
  }

}
