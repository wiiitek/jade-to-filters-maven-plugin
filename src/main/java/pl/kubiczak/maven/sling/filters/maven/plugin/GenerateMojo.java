package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
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
   * Location of the file.
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

    Output fo = new Output(outputFile, getLog());

    Writer writer = fo.createWriter();
    try {
      writer.close();
    } catch (IOException ioe) {
      getLog().error("Error while closing output file");
    }
  }
}
