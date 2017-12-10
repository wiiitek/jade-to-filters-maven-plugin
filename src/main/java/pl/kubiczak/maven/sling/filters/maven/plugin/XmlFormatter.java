package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;

class XmlFormatter {

  private final Log mavenLog;

  public XmlFormatter(Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  String format(Document document) {
    String xml;
    try {
      DOMSource domSource = new DOMSource(document);
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);

      createTransformer().transform(domSource, result);

      writer.flush();
      xml = writer.toString();
    } catch (TransformerConfigurationException tce) {
      mavenLog.error("error while creating new transformer", tce);
      xml = "<!-- error while creating new transformer -->\n";
    } catch (TransformerException te) {
      mavenLog.error("error while transforming XML document", te);
      xml = "<!-- error while transforming XML document -->\n";
    }
    // always change to linux newlines
    return xml.replace("\r\n", "\n");
  }

  private Transformer createTransformer() throws TransformerConfigurationException {
    Integer indentNumber = 2;
    return new XmlTransformerBuilder(mavenLog)
        .addFactoryAttribute("indent-number", indentNumber)
        .addOutputProperty(OutputKeys.METHOD, "xml")
        .addOutputProperty(OutputKeys.ENCODING, "UTF-8")
        .addOutputProperty(OutputKeys.INDENT, "yes")
        // for newline after XML declaration - https://stackoverflow.com/a/18251901
        .addOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes")
        .build();
  }
}
