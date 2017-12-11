package pl.kubiczak.maven.sling.filters.maven.plugin;

import de.neuland.jade4j.Jade4J;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import org.apache.maven.plugin.logging.Log;

/**
 * Transforms Jade file into XML string.
 */
class JadeReader {

  private final Log mavenLog;

  JadeReader(Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  String transformToXml(URL jadeFileUrl) {
    String result = null;
    mavenLog.debug("transforming Jade file from URL: '" + jadeFileUrl + "'");
    try {
      result = Jade4J.render(jadeFileUrl, new HashMap<String, Object>(), true);
      // we don't want to have any whitespaces here because they are parsed later into XML
      result = "<filters>" + result + "</filters>";
    } catch (IOException e) {
      mavenLog.error("error while parsing '" + jadeFileUrl + "' as Jade file. " + e.getMessage());
    }
    return result;
  }

}
