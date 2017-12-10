package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
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
      // the default value is in sync with maven project created from this archetype
      // https://github.com/Adobe-Marketing-Cloud/aem-project-archetype
      defaultValue = "${project.basedir}/src/main/content/META-INF/vault/filter.xml",
      required = true
  )
  private File outputFile;

  /**
   * Executes the mojo for Sling filters generation.
   */
  public void execute() throws MojoExecutionException {

    Output output = new Output(getLog(), outputFile);

    XmlSlingFilters slingFilters = new XmlSlingFilters(getLog());

    for (String jadeFilename : inputFiles) {
      slingFilters.addFromFile(jadeFilename);
    }

    Writer writer = output.createWriter();
    try {
      writer.write(slingFilters.prettyXml());
      writer.close();
    } catch (IOException ioe) {
      throw new MojoExecutionException("error while writing into output file");
    }
  }
}
