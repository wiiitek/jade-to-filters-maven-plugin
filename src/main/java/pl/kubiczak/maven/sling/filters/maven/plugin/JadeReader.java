package pl.kubiczak.maven.sling.filters.maven.plugin;

import de.neuland.jade4j.Jade4J;
import java.io.IOException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;

/**
 * Transforms Jade file into XML string.
 */
class JadeReader {

  private final Log mavenLog;

  JadeReader(Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  String transformToXml(URL jadeFilterUrl) {
    String result = null;
    mavenLog.debug("transforming a template with URL: '" + jadeFilterUrl + "'");
    try {
      result = Jade4J.render(jadeFilterUrl, null, true);
      // we don't want to have any whitespaces here because they are parsed later into XML
      result = "<filters>" + result + "</filters>";
    } catch (IOException e) {
      mavenLog.error("error while parsing '" + jadeFilterUrl + "' as Jade file. " + e.getMessage());
    }
    return result;
  }

}
