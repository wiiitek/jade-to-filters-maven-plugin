package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Generates filter file for content package from Jade files.
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
      property = "filters.inputFiles"
  )
  private List<String> inputFiles;

  /**
   * Location of the output file.
   */
  @Parameter(
      property = "filters.outputFile",
      // the default value is in sync with maven project created from this archetype
      // https://github.com/Adobe-Marketing-Cloud/aem-project-archetype
      defaultValue = "${project.basedir}/src/main/content/META-INF/vault/filter.xml",
      required = true
  )
  private File outputFile;

  /**
   * Executes the mojo for filters generation.
   */
  public void execute() throws MojoExecutionException {

    FiltersCreator filtersCreator = new FiltersCreator(getLog());

    for (String jadeFilename : inputFiles) {
      filtersCreator.addFromFile(jadeFilename);
    }

    // TODO: can I use Path right from the beginning as a parameter?
    Path path = outputFile.toPath();
    OutputFileWriter outputFileWriter = new OutputFileWriter(getLog(), path);
    outputFileWriter.write(filtersCreator.prettyXml());
  }
}
