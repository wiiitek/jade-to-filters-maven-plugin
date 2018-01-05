package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

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
class OutputFileWriter {

  private final Log mavenLog;

  private final File outputFile;

  OutputFileWriter(Log mavenLog, File outputFile) {
    this.mavenLog = mavenLog;
    this.outputFile = outputFile;
  }

  void write(String fileContent) throws MojoExecutionException {
    FileOutputStream os = null;
    Writer writer = null;
    String outputFilePath = getPath(outputFile);
    createParentDirectories(outputFile, outputFilePath);
    deleteIfExists(outputFile, outputFilePath);
    mavenLog.debug("Creating writer for :'" + outputFilePath + "'");
    try {
      os = new FileOutputStream(outputFilePath);
      writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      checkContentAndWrite(fileContent, writer, outputFilePath);
    } catch (FileNotFoundException fnfe) {
      logAndThrow("File not found: '" + outputFilePath + "'", fnfe);
    } catch (UnsupportedEncodingException uee) {
      logAndThrow("Unsupported encoding: UTF-8", uee);
    } finally {
      closeIfNotNull(writer);
      closeIfNotNull(os);
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

  private void createParentDirectories(File file, String path)
      throws MojoExecutionException {
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

  private void deleteIfExists(File file, String path) {
    boolean deleted = file.delete();
    if (deleted) {
      mavenLog.debug("The old version of '" + path + "' file was deleted.");
    }
  }

  private void checkContentAndWrite(String content, Writer writer, String path)
      throws MojoExecutionException {
    try {
      if (content != null) {
        writer.write(content);
      } else {
        String msg = "Will create empty file for content: null at: '" + path + "'";
        mavenLog.info(msg);
      }
    } catch (IOException ioe) {
      logAndThrow("Error while writing into output file", ioe);
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
}
