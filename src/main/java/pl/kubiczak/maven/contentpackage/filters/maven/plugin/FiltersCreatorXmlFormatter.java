package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.io.IOException;
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
class FiltersCreatorXmlFormatter {

  private static final Integer XML_INDENT = 2;

  private final Log mavenLog;

  public FiltersCreatorXmlFormatter(Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  String format(Document document) {
    String xml;

    StringWriter writer = null;
    try {
      cleanBlankTextNodes(document);

      DOMSource domSource = new DOMSource(document);
      writer = new StringWriter();
      StreamResult result = new StreamResult(writer);

      createTransformer().transform(domSource, result);

      writer.flush();
      xml = writer.toString();
    } catch (TransformerConfigurationException tce) {
      mavenLog.error("Error while creating new transformer!", tce);
      xml = "<!-- error while creating new transformer -->\n";
    } catch (TransformerException te) {
      mavenLog.error("Error while transforming XML document!", te);
      xml = "<!-- error while transforming XML document -->\n";
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException ioe) {
          mavenLog.error("Error while closing string writer: " + ioe.getMessage(), ioe);
        }
      }
    }
    // always change to linux newlines
    return xml.replace("\r\n", "\n");
  }

  private Transformer createTransformer() throws TransformerConfigurationException {
    TransformerFactoryBuilder transformerFactoryBuilder = new TransformerFactoryBuilder(mavenLog)
        .addFactoryAttribute("indent-number", XML_INDENT);
    TransformerBuilder transformerBuilder =
        new TransformerBuilder(mavenLog, transformerFactoryBuilder)
            .addOutputProperty(OutputKeys.METHOD, "xml")
            .addOutputProperty(OutputKeys.ENCODING, "UTF-8")
            .addOutputProperty(OutputKeys.INDENT, "yes")
            .addOutputProperty("{http://xml.apache.org/xslt}indent-amount", XML_INDENT.toString())
            .addOutputProperty("{http://xml.apache.org/xalan}indent-amount", XML_INDENT.toString())
            // for newline after XML declaration - https://stackoverflow.com/a/18251901
            .addOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
    Transformer transformer = transformerBuilder.create();
    mavenLog.debug("Transformer created");
    return transformer;
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
      mavenLog.error("Error while compiling XPATH: '" + expression + "'!");
    }
    NodeList blankTextNodes;
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
        mavenLog.error("Error while evaluating XPATH: '" + xpath + "'!");
      }
    }
  }
}
