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
    boolean notSimpleFile = !file.isFile();
    if (notSimpleFile) {
      if (file.exists()) {
        mavenLog.error("Cannot send output to " + file + " as it exists but is not a file.");
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
  }

  void deleteFileIfExists() {
    boolean deleted = file.delete();
    if (deleted) {
      mavenLog.debug("The old version of '" + path + "' file was deleted.");
    }
  }

  private String getPath(File file) {
    String path = null;
    try {
      path = file.getCanonicalPath();
    } catch (IOException e) {
      mavenLog.debug("error while getting canonical path. will try to get absolute path.");
    }
    if (path == null) {
      try {
        path = file.getAbsolutePath();
      } catch (SecurityException se) {
        String msg = "error while getting absolute path for '" + file + "'.";
        msg += " do you have the correct permissions for the file?";
        mavenLog.error(msg);
      }
    }
    return path;
  }
}
