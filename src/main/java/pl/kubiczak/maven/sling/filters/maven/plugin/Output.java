package pl.kubiczak.maven.sling.filters.maven.plugin;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Creates output file.
 */
class Output {

  private final Log mavenLog;

  private final File file;

  Output(Log mavenLog, File file) {
    this.mavenLog = mavenLog;
    this.file = file;
  }

  void write(String fileContent) throws MojoExecutionException {
    FileOutputStream os = null;
    Writer writer = null;
    String path = getPath(file);
    createDirectories(file);
    file.delete();
    mavenLog.debug("Creating writer for :'" + path + "'");
    try {
      os = new FileOutputStream(path);
      writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      writer.write(fileContent);
    } catch (FileNotFoundException fnfe) {
      logAndThrow("File not found: '" + path + "'", fnfe);
    } catch (UnsupportedEncodingException uee) {
      logAndThrow("Unsupported encoding: UTF-8", uee);
    } catch (IOException ioe) {
      logAndThrow("error while writing into output file", ioe);
    } finally {
      closeIfNotNull(writer);
      closeIfNotNull(os);
    }
  }

  private void logAndThrow(String errorMessage, Exception ex) throws MojoExecutionException {
    mavenLog.error(errorMessage);
    throw new MojoExecutionException(errorMessage, ex);
  }

  private void closeIfNotNull(Closeable output) {
    if (output != null) {
      try {
        output.close();
      } catch (IOException e) {
        //
      }
    }
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
        mavenLog.error("Cannot send output to " + file + " as it exists but is not a file.");
      } else if (!file.getParentFile().isDirectory()) {
        if (!file.getParentFile().mkdirs()) {
          throw new MojoExecutionException("Can not create folders for a file: '" + file + "'");
        }
      }
    }
  }

}
