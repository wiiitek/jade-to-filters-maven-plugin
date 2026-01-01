package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutputFileWriterTest {

  private static final String SPECIAL_CHARS_CONTENT = "źół\n!@#$%^&*()_+ \";\nGrüße\t";

  private static final String SPECIAL_ENDING_CONTENT = "\nHi\nThere\n";

  @Mock
  private Log mavenLogMock;

  @Captor
  ArgumentCaptor<String> errorMessageCaptor;

  private Path outputFile;

  /**
   * Creates random File instance in tmp folder, but not actual file on filesystem.
   */
  @BeforeEach
  void createsRandomPathForOutputFile(@TempDir Path tempDir) {
    String randomFileName = new RandomFilename().getNext("xml");
    outputFile = tempDir.resolve(randomFileName);
    assertThat(Files.exists(outputFile))
        .as("Random path should not point at any file")
        .isFalse();
  }

  /**
   * Deletes random file after tests.
   */
  @AfterEach
  void deleteCreatedFileIfExists() throws IOException {
    Files.deleteIfExists(outputFile);
    assertThat(Files.exists(outputFile))
        .as("The output file should be cleaned-up (deleted) after test!")
        .isFalse();
  }

  @Test
  void write_shouldCreateEmptyFileForNull() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(null);

    assertThat(Files.exists(outputFile)).as("Output fle should exist").isTrue();
    assertThat(Files.isRegularFile(outputFile)).as("Output file should be a file").isTrue();
    verifyFileContent(outputFile, StringUtils.EMPTY);
  }

  @Test
  void write_shouldCreateFileForEmptyString() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(StringUtils.EMPTY);

    assertThat(Files.exists(outputFile)).as("Output fle should exist").isTrue();
    assertThat(Files.isRegularFile(outputFile)).as("Output file should be a file").isTrue();
    verifyFileContent(outputFile, StringUtils.EMPTY);
  }

  @Test
  void write_shouldCreateFileWithNewlineAtTheEnd() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(SPECIAL_ENDING_CONTENT);

    assertThat(Files.exists(outputFile)).as("Output fle should exist").isTrue();
    assertThat(Files.isRegularFile(outputFile)).as("Output file should be a file").isTrue();
    verifyFileContent(outputFile, SPECIAL_ENDING_CONTENT);
  }

  @Test
  void write_shouldCreateFileWithSpecialChars() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(SPECIAL_CHARS_CONTENT);

    assertThat(Files.exists(outputFile)).as("Output fle should exist").isTrue();
    assertThat(Files.isRegularFile(outputFile)).as("Output file should be a file").isTrue();
    verifyFileContent(outputFile, SPECIAL_CHARS_CONTENT);
  }

  @Test
  void shouldLogMessageWhenFileIsNotFound(@TempDir Path existingDirectory) {
    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, existingDirectory);

    try {
      tested.write(SPECIAL_CHARS_CONTENT);
      fail("Expected exception to be thrown");
    } catch (MojoExecutionException e) {
      // ignore exception in this test
    }

    verify(mavenLogMock, times(2)).error(errorMessageCaptor.capture());
    List<String> actual = errorMessageCaptor.getAllValues();
    assertThat(actual).hasSize(2);

    String expectedPathInMessage = existingDirectory.toAbsolutePath().toString();
    String first = actual.get(0);
    assertThat(first).startsWith("Cannot delete '");
    assertThat(first).contains(expectedPathInMessage);
    assertThat(first).endsWith("' as it exists but is not a file.");

    String second = actual.get(1);
    assertThat(second).startsWith("File not found or is a folder: '");
    assertThat(second).contains(expectedPathInMessage);
    assertThat(second).endsWith("'");
  }

  @Test
  void shouldThrowExceptionWhenFileIsNotFound(@TempDir Path existingDirectory) {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, existingDirectory);

    try {
      tested.write(SPECIAL_CHARS_CONTENT);
      fail("Expected exception to be thrown");
    } catch (Exception e) {
      assertThat(e).isInstanceOf(MojoExecutionException.class);
      assertThat(e.getMessage()).contains("File not found or is a folder");
    }
  }

  private void verifyFileContent(Path path, String expectedContent) {
    InputStream is = null;
    String actualContent = null;
    try {
      is = Files.newInputStream(path);
      actualContent = read(is);
    } catch (FileNotFoundException e) {
      fail("File " + path + "should be created!");
    } catch (IOException e) {
      fail("Cannot read content of file " + path);
    } finally {
      closeInputStream(is);
    }
    assertThat(actualContent).as("File content should be as expected!").isEqualTo(expectedContent);
  }

  private String read(InputStream is) {
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(is, writer, "UTF-8");
    } catch (IOException e) {
      throw new RuntimeException("Unsupported encoding: UTF-8");
    }
    return writer.toString();
  }

  private void closeInputStream(InputStream is) {
    if (is != null) {
      try {
        is.close();
      } catch (IOException e) {
        throw new RuntimeException("Error while closing input stream for output file.");
      }
    }
  }
}
