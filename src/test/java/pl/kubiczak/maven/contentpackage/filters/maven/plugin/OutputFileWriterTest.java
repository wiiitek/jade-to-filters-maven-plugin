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
import java.util.Random;
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

  @Before
  public void createNonExistingOutputFile() {
    outputFile = new File(folder.getRoot().getAbsolutePath() + getRandomName());
    assert !outputFile.exists() : "Created file should not exist";
  }

  @After
  public void deleteCreatedFileIfExists() {
    boolean deleted = outputFile.delete();
    assert deleted : "Test should clean-up (delete) the output file!";
  }

  @Test
  public void shouldCreateEmptyFileForNull() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(null);

    assertThat("Output fle should exist", outputFile.exists(), is(true));
    assertThat("Output file should be a file", outputFile.isFile(), is(true));
    verifyFileContent(outputFile.getAbsolutePath(), StringUtils.EMPTY);
  }

  @Test
  public void shouldCreateFileForEmptyString() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(StringUtils.EMPTY);

    assertThat("Output fle should exist", outputFile.exists(), is(true));
    assertThat("Output file should be a file", outputFile.isFile(), is(true));
    verifyFileContent(outputFile.getAbsolutePath(), StringUtils.EMPTY);
  }

  @Test
  public void shouldCreateFileWithNewlineAtTheEnd() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(SPECIAL_ENDING_CONTENT);

    assertThat("Output fle should exist", outputFile.exists(), is(true));
    assertThat("Output file should be a file", outputFile.isFile(), is(true));
    verifyFileContent(outputFile.getAbsolutePath(), SPECIAL_ENDING_CONTENT);
  }

  @Test
  public void shouldCreateFileWithSpecialChars() throws MojoExecutionException {

    OutputFileWriter tested = new OutputFileWriter(mavenLogMock, outputFile);

    tested.write(SPECIAL_CHARS_CONTENT);

    assertThat("Output fle should exist", outputFile.exists(), is(true));
    assertThat("Output file should be a file", outputFile.isFile(), is(true));
    verifyFileContent(outputFile.getAbsolutePath(), SPECIAL_CHARS_CONTENT);
  }

  private String getRandomName() {
    Integer randomInt = new Random(System.currentTimeMillis()).nextInt();
    long number = Math.abs(randomInt.longValue());
    return String.format("file-%d.xml", number);
  }

  private void verifyFileContent(String path, String expectedContent) {
    FileInputStream is = null;
    try {
      is = new FileInputStream(path);
    } catch (FileNotFoundException e) {
      fail("File " + path + "should be created");
    }
    String actualContent = read(is);
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
}
