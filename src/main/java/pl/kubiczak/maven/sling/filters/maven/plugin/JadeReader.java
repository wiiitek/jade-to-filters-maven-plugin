package pl.kubiczak.maven.sling.filters.maven.plugin;

import de.neuland.jade4j.Jade4J;
import java.io.IOException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms Jade file into XML string.
 */
class JadeReader {

  private static final Logger LOG = LoggerFactory.getLogger(JadeReader.class);

  private final Log mavenLog;

  JadeReader(Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  String transformToXml(URL jadeFilterUrl) {
    String result = null;
    LOG.debug("transforming a template with URL: '{}'", jadeFilterUrl);
    try {
      result = Jade4J.render(jadeFilterUrl, null, true);
    } catch (IOException e) {
      mavenLog.error("error while parsing '" + jadeFilterUrl + "' as Jade file. " + e.getMessage());
    }
    return result;
  }

}
