package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
public class OutputFileWriterTest {

  private static final String SPECIAL_CHARS_CONTENT = "źół\n!@#$%^&*()_+ \";\nGrüße\t";

  private static final String SPECIAL_ENDING_CONTENT = "\nHi\nThere\n";

  @Mock
  private Log mavenLogMock;

  @Captor
  ArgumentCaptor<String> errorMessageCaptor;

  private File outputFile;

  /**
   * Creates random File instance in tmp folder, but not actual file on filesystem.
   */
  @BeforeEach
  public void createNonExistingOutputFile(@TempDir Path tempDir) throws IOException {
    String randomFileName = new RandomFilename().getNext("xml");
    outputFile = tempDir.resolve(randomFileName).toFile();
    assert !outputFile.exists() : "Created file should not exist";
  }

  @AfterEach
  public void deleteCreatedFileIfExists() {
    boolean isNoThere = !outputFile.exists() || outputFile.delete();
    assert isNoThere : "The output file should cleaned-up (deleted) after test!";
  }

  @Test
  public void write_shouldCreateEmptyFileForNull() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(null);

    assertThat(outputFile.exists()).as("Output fle should exist").isTrue();
    assertThat(outputFile.isFile()).as("Output file should be a file").isTrue();
    verifyFileContent(outputFile.getAbsolutePath(), StringUtils.EMPTY);
  }

  @Test
  public void write_shouldCreateFileForEmptyString() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(StringUtils.EMPTY);

    assertThat(outputFile.exists()).as("Output fle should exist").isTrue();
    assertThat(outputFile.isFile()).as("Output file should be a file").isTrue();
    verifyFileContent(outputFile.getAbsolutePath(), StringUtils.EMPTY);
  }

  @Test
  public void write_shouldCreateFileWithNewlineAtTheEnd() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(SPECIAL_ENDING_CONTENT);

    assertThat(outputFile.exists()).as("Output fle should exist").isTrue();
    assertThat(outputFile.isFile()).as("Output file should be a file").isTrue();
    verifyFileContent(outputFile.getAbsolutePath(), SPECIAL_ENDING_CONTENT);
  }

  @Test
  public void write_shouldCreateFileWithSpecialChars() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(SPECIAL_CHARS_CONTENT);

    assertThat(outputFile.exists()).as("Output fle should exist").isTrue();
    assertThat(outputFile.isFile()).as("Output file should be a file").isTrue();
    verifyFileContent(outputFile.getAbsolutePath(), SPECIAL_CHARS_CONTENT);
  }

  @Test
  public void shouldLogMessageWhenFileIsNotFound(@TempDir Path tempDir) {
    String folderPath = tempDir.toAbsolutePath().toString();
    File existingDirectory = new File(folderPath);

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

    String first = actual.get(0);
    assertThat(first).startsWith("Cannot delete '");
    assertThat(first).contains(folderPath);
    assertThat(first).endsWith("' as it exists but is not a file.");

    String second = actual.get(1);
    assertThat(second).startsWith("File not found or is a folder: '");
    assertThat(second).contains(folderPath);
    assertThat(second).endsWith("'");
  }

  @Test
  public void shouldThrowExceptionWhenFileIsNotFound(@TempDir Path tempDir) {
    String folderPath = tempDir.toAbsolutePath().toString();
    File existingDirectory = new File(folderPath);

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, existingDirectory);

    try {
      tested.write(SPECIAL_CHARS_CONTENT);
      fail("Expected exception to be thrown");
    } catch (Exception e) {
      assertThat(e).isInstanceOf(MojoExecutionException.class);
      assertThat(e.getMessage()).contains("File not found or is a folder");
    }
  }

  private void verifyFileContent(String path, String expectedContent) {
    FileInputStream is = null;
    String actualContent = null;
    try {
      is = new FileInputStream(path);
      actualContent = read(is);
    } catch (FileNotFoundException e) {
      fail("File " + path + "should be created!");
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

  private void closeInputStream(FileInputStream is) {
    if (is != null) {
      try {
        is.close();
      } catch (IOException e) {
        throw new RuntimeException("Error while closing input stream for output file.");
      }
    }
  }
}
