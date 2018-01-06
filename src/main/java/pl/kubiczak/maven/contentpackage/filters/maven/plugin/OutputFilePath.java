package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

class OutputFilePath {

  private final Log mavenLog;

  private final File file;

  private final String path;

  OutputFilePath(Log mavenLog, File file) {
    this.mavenLog = mavenLog;
    this.file = file;
    this.path = getPath(file);
  }

  String get() {
    return path;
  }

  void createParentDirectories() throws MojoExecutionException {
    if (file.exists()) {
      if (!file.isFile()) {
        String msg = "Cannot send output to '" + file + "' as it exists but is not a file.";
        mavenLog.error(msg);
      }
    } else {
      boolean parentFolderExists = file.getParentFile().isDirectory();
      if (!parentFolderExists) {
        mavenLog.info("Creating directories for path: '" + path + "'.");
        boolean parentFoldersCreated = file.getParentFile().mkdirs();
        if (!parentFoldersCreated) {
          throw new MojoExecutionException("Can not create folders for a file: '" + file + "'");
        } else {
          mavenLog.debug("Directories created for path: '" + path + "'");
        }
      }
    }
  }

  void deleteFileIfExists() {
    if (file.exists()) {
      if (!file.isFile()) {
        String msg = "Cannot delete '" + path + "' as it exists but is not a file.";
        mavenLog.error(msg);
      } else {
        boolean deleted = file.delete();
        mavenLog.debug("File '" + path + "' was deleted. Success: '" + deleted + "'.");
      }
    }
  }

  private String getPath(File file) {
    String result = null;
    try {
      result = file.getCanonicalPath();
    } catch (IOException e) {
      mavenLog.debug("Error while getting canonical path. will try to get absolute path.");
    }
    if (result == null) {
      try {
        result = file.getAbsolutePath();
      } catch (SecurityException se) {
        String msg = "Error while getting absolute path for '" + file + "'.";
        msg += " do you have the correct permissions for the file?";
        mavenLog.error(msg);
      }
    }
    return result;
  }
}
