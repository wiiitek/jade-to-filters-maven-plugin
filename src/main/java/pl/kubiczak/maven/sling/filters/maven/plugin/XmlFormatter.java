package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Formats XML for filter file.
 */
class XmlFormatter {

  private final Log mavenLog;

  public XmlFormatter(Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  String format(Document document) {
    String xml;
    try {
      cleanBlankTextNodes(document);

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
        .addOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        .addOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2")
        // for newline after XML declaration - https://stackoverflow.com/a/18251901
        .addOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes")
        .build();
  }

  /**
   * Cleans blank text nodes for better formatting - https://stackoverflow.com/a/25866191.
   *
   * @param document document that will have the nodes removed
   */
  private void cleanBlankTextNodes(Document document) {
    XPath xpath = XPathFactory.newInstance().newXPath();
    String expression = "//text()[normalize-space(.) = '']";
    XPathExpression xpathExpression = null;
    try {
      xpathExpression = xpath.compile(expression);
    } catch (XPathExpressionException e) {
      mavenLog.error("error while compiling XPATH: '" + expression + "'");
    }
    NodeList blankTextNodes = null;
    if (xpathExpression != null) {
      try {
        blankTextNodes = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
        for (int i = 0; i < blankTextNodes.getLength(); i++) {
          Node parent = blankTextNodes.item(i).getParentNode();
          if (parent != null) {
            parent.removeChild(blankTextNodes.item(i));
          }
        }
      } catch (XPathExpressionException e) {
        mavenLog.error("error while evaluating XPATH: '" + xpath + "'");
      }
    }
  }
}
