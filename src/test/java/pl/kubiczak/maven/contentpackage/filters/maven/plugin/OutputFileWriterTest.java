package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OutputFileWriterTest {

  private static final String SPECIAL_CHARS_CONTENT = "źół\n!@#$%^&*()_+ \";\nGrüße\t";

  private static final String SPECIAL_ENDING_CONTENT = "\nHi\nThere\n";

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Mock
  private Log mavenLogMock;

  private File outputFile;

  /**
   * Creates random File instance in tmp folder, but not actual file on filesystem.
   */
  @Before
  public void createNonExistingOutputFile() {
    String randomFileName = new RandomFilename().getNext("xml");
    outputFile = new File(folder.getRoot().getAbsolutePath() + "/" + randomFileName);
    assert !outputFile.exists() : "Created file should not exist";
  }

  @After
  public void deleteCreatedFileIfExists() {
    boolean deleted = outputFile.delete();
    assert deleted : "Test should clean-up (delete) the output file!";
  }

  @Test
  public void write_shouldCreateEmptyFileForNull() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(null);

    assertThat("Output fle should exist", outputFile.exists(), is(true));
    assertThat("Output file should be a file", outputFile.isFile(), is(true));
    verifyFileContent(outputFile.getAbsolutePath(), StringUtils.EMPTY);
  }

  @Test
  public void write_shouldCreateFileForEmptyString() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(StringUtils.EMPTY);

    assertThat("Output fle should exist", outputFile.exists(), is(true));
    assertThat("Output file should be a file", outputFile.isFile(), is(true));
    verifyFileContent(outputFile.getAbsolutePath(), StringUtils.EMPTY);
  }

  @Test
  public void write_shouldCreateFileWithNewlineAtTheEnd() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(SPECIAL_ENDING_CONTENT);

    assertThat("Output fle should exist", outputFile.exists(), is(true));
    assertThat("Output file should be a file", outputFile.isFile(), is(true));
    verifyFileContent(outputFile.getAbsolutePath(), SPECIAL_ENDING_CONTENT);
  }

  @Test
  public void write_shouldCreateFileWithSpecialChars() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(SPECIAL_CHARS_CONTENT);

    assertThat("Output fle should exist", outputFile.exists(), is(true));
    assertThat("Output file should be a file", outputFile.isFile(), is(true));
    verifyFileContent(outputFile.getAbsolutePath(), SPECIAL_CHARS_CONTENT);
  }

  private void verifyFileContent(String path, String expectedContent) {
    FileInputStream is = null;
    String actualContent = null;
    try {
      is = new FileInputStream(path);
      actualContent = read(is);
    } catch (FileNotFoundException e) {
      fail("File " + path + "should be created");
    } finally {
      closeInputStream(is);
    }
    assertThat("File content should be as expected", actualContent, equalTo(expectedContent));
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
