package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

class Output {

  private final File file;

  private final Log log;

  Output(File file, Log log) {
    this.file = file;
    this.log = log;
  }

  Writer createWriter() throws MojoExecutionException {
    Writer result = null;
    String path = getPath(file);
    createDirectories(file);
    file.delete();
    log.debug("Creating writer for :'" + path + "'");
    try {
      FileOutputStream os = new FileOutputStream(path);
      result = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
    } catch (FileNotFoundException fnfe) {
      log.error("File not found: '" + path + "'", fnfe);
    } catch (UnsupportedEncodingException uee) {
      log.error("Unsupported encoding: UTF-8");
    }
    return result;
  }

  private String getPath(File f) {
    String path;
    try {
      path = f.getCanonicalPath();
    } catch (IOException e) {
      path = f.getAbsolutePath();
    }
    return path;
  }

  private void createDirectories(File file) throws MojoExecutionException {
    boolean notSimpleFile = !file.isFile();
    if (notSimpleFile) {
      if (file.exists()) {
        log.error("Cannot send output to " + file + " as it exists but is not a file.");
      } else if (!file.getParentFile().isDirectory()) {
        if (!file.getParentFile().mkdirs()) {
          throw new MojoExecutionException("Can not create folders for a file: '" + file + "'");
        }
      }
    }
  }

}
