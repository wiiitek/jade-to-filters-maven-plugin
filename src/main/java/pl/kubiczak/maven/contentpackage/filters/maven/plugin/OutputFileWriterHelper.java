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
    return path.toAbsolutePath().toString();
  }

  void createParentDirectories() throws MojoExecutionException {
    Path parent = path.getParent();
    if (!Files.isDirectory(path)) {
      mavenLog.info("Creating directories for path: '" + path + "'.");
      try {
        Files.createDirectories(parent);
        mavenLog.debug("Directories created for path: '" + path + "'");
      } catch (IOException e) {
        throw new MojoExecutionException("Couldn't create folder for a file: '" + path + "'", e);
      }
    }
  }

  void deleteFileIfExists() throws MojoExecutionException {
    if (Files.exists(path)) {
      if (!Files.isRegularFile(path)) {
        String msg = "Cannot delete '" + path + "' as it exists but is not a file.";
        mavenLog.error(msg);
      } else {
        try {
          boolean deleted = Files.deleteIfExists(path);
          mavenLog.debug("File '" + path + "' was deleted. Success: '" + deleted + "'.");
        } catch (IOException e) {
          throw new MojoExecutionException("Couldn't delete a file: '" + path + "'", e);
        }
      }
    }
  }
}
