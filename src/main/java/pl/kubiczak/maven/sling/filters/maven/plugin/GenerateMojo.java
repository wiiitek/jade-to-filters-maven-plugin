package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Says "Hi" to the user.
 */
@Mojo(
    name = "generate",
    defaultPhase = LifecyclePhase.GENERATE_RESOURCES
)
public class GenerateMojo extends AbstractMojo {

  /**
   * Location of the Jade input files.
   */
  @Parameter(
      required = true,
      property = "sling.filters.inputFiles"
  )
  private List<String> inputFiles;

  /**
   * Location of the output file.
   */
  @Parameter(
      property = "sling.filters.outputFile",
      defaultValue = "${project.basedir}/src/main/content/META-INF/vault/filter.xml",
      required = true
  )
  private File outputFile;

  /**
   * Executes the mojo for Sling filters generation.
   */
  public void execute() throws MojoExecutionException {

    Output output = new Output(outputFile, getLog());

    XmlSlingFilters slingFilters = new XmlSlingFilters();
    for (String jadeFilename : inputFiles) {
      try {
        URL jadeFilterUrl = new File(jadeFilename).toURI().toURL();
        slingFilters.addFromFile(jadeFilterUrl);
      } catch (IOException e) {
        getLog().error("error while adding filter from '" + jadeFilename + "'");
      }
    }

    Writer writer = output.createWriter();
    try {
      writer.write(slingFilters.prettyXml());
      writer.close();
    } catch (IOException ioe) {
      getLog().error("Error while closing output file");
    }
  }
}
