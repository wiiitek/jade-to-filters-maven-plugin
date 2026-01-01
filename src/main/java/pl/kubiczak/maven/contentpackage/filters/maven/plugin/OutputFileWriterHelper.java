package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

class OutputFileWriterHelper {

  private final Log mavenLog;

  private final Path path;

  OutputFileWriterHelper(Log mavenLog, Path path) {
    this.mavenLog = mavenLog;
    this.path = path;
  }

  String getFilePath() {
    return path.toString();
  }

  void createParentDirectories() throws MojoExecutionException {
    Path parent = path.getParent();
    if (!Files.exists(parent)) {
      mavenLog.info("Creating directories for path: '" + parent + "'.");
      try {
        Files.createDirectories(parent);
        mavenLog.debug("Directories created for path: '" + parent + "'");
      } catch (IOException e) {
        throw new MojoExecutionException("Couldn't create folder: '" + parent + "'", e);
      }
    }
  }

  void deleteFileIfExists() throws MojoExecutionException {
    if (Files.exists(path)) {
      boolean regularFile = Files.isRegularFile(path);
      if (regularFile) {
        try {
          boolean deleted = Files.deleteIfExists(path);
          if (deleted) {
            mavenLog.debug("File '" + path + "' was deleted.");
          }
        } catch (IOException e) {
          throw new MojoExecutionException("Couldn't delete a file: '" + path + "'", e);
        }
      } else {
        String msg = "Cannot delete '" + path + "' as it exists but is not a file.";
        mavenLog.error(msg);
      }
    }
  }
}
