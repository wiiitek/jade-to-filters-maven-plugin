package pl.kubiczak.maven.sling.filters.maven.plugin.xml;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class Pretty {

  private static final Logger LOG = LoggerFactory.getLogger(Pretty.class);

  public String format(Document document) {
    String xml;
    try {
      DOMSource domSource = new DOMSource(document);
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);

      createTransformer().transform(domSource, result);

      writer.flush();
      xml = writer.toString();
    } catch (TransformerConfigurationException tce) {
      LOG.error("error while creating new transformer", tce);
      xml = "<!-- error while creating new transformer -->\n";
    } catch (TransformerException te) {
      LOG.error("error while transforming XML document", te);
      xml = "<!-- error while transforming XML document -->\n";
    }
    // always change to linux newlines
    return xml.replace("\r\n", "\n");
  }

  private Transformer createTransformer() throws TransformerConfigurationException {
    Integer indentNumber = 2;
    return new XmlTransformerBuilder()
        .addFactoryAttribute(TransformerFactoryImpl.INDENT_NUMBER, indentNumber)
        .addOutputProperty(OutputKeys.METHOD, "xml")
        .addOutputProperty(OutputKeys.ENCODING, "UTF-8")
        .addOutputProperty(OutputKeys.INDENT, "yes")
        // for newline after XML declaration - https://stackoverflow.com/a/18251901
        .addOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes")
        .build();
  }
}
