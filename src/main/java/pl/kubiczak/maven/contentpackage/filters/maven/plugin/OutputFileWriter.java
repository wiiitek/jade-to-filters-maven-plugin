package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Creates output file.
 */
class OutputFileWriter {

  private final Log mavenLog;

  private final Path outputFile;

  OutputFileWriter(Log mavenLog, Path outputFile) {
    this.mavenLog = mavenLog;
    this.outputFile = outputFile;
  }

  void write(String fileContent) throws MojoExecutionException {
    OutputFileWriterHelper helper = new OutputFileWriterHelper(mavenLog, outputFile);
    helper.createParentDirectories();
    helper.deleteFileIfExists();
    String path = helper.getFilePath();
    mavenLog.debug("Prepare filesystem for writing to:'" + path + "'");
    try (Writer writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), UTF_8))
    ) {

      checkContentAndWrite(fileContent, writer, path);
    } catch (FileNotFoundException fnfe) {
      logAndThrow("File not found or is a folder: '" + path + "'", fnfe);
    } catch (IOException ioe) {
      logAndThrow("IO exception for: '" + path + "'", ioe);
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
