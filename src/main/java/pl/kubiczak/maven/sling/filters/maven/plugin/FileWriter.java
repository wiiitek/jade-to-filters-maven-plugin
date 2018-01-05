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
class FileWriter {

  private final Log mavenLog;

  private final File file;

  FileWriter(Log mavenLog, File file) {
    this.mavenLog = mavenLog;
    this.file = file;
  }

  void write(String fileContent) throws MojoExecutionException {
    FileOutputStream os = null;
    Writer writer = null;
    String path = getPath();
    createDirectories(file);
    boolean deleted = file.delete();
    if (deleted) {
      mavenLog.debug("The old version of '" + path + "' file was deleted.");
    }
    mavenLog.debug("Creating writer for :'" + path + "'");
    try {
      os = new FileOutputStream(path);
      writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      if (fileContent != null) {
        writer.write(fileContent);
      } else {
        mavenLog.info("Will create empty file for content: null at: '" + file.getPath() + "'");
      }
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

  private String getPath() {
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

  private void createDirectories(File file) throws MojoExecutionException {
    boolean notSimpleFile = !file.isFile();
    if (notSimpleFile) {
      if (file.exists()) {
        mavenLog.error("Cannot send output to " + file + " as it exists but is not a file.");
      } else {
        boolean parentFolderExists = file.getParentFile().isDirectory();
        if (!parentFolderExists) {
          boolean parentFoldersCreated = file.getParentFile().mkdirs();
          if (!parentFoldersCreated) {
            throw new MojoExecutionException("Can not create folders for a file: '" + file + "'");
          }
        }
      }
    }
  }
}