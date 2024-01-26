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
import java.nio.charset.StandardCharsets;
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

    OutputFileWriterHelper helper = new OutputFileWriterHelper(mavenLog, outputFile);
    helper.createParentDirectories();
    helper.deleteFileIfExists();
    String path = helper.getFilePath();
    mavenLog.debug("Prepare filesystem for writing to:'" + path + "'");
    try {
      os = new FileOutputStream(path);
      writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
      checkContentAndWrite(fileContent, writer, path);
    } catch (FileNotFoundException fnfe) {
      logAndThrow("File not found or is a folder: '" + path + "'", fnfe);
    } finally {
      closeIfNotNull(writer);
      closeIfNotNull(os);
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
