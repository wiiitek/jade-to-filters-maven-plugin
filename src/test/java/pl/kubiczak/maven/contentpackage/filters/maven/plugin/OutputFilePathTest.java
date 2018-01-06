package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OutputFilePathTest {

  private static final String LONG_PATH_TO_FILE = "/some/path/to/a/file.xml";

  private static final int ONCE = 1;
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Mock
  private Log mavenLogMock;

  @Test
  public void createParentDirectories_shouldNotCreateAFile() throws MojoExecutionException {
    String tmpFolderPath = folder.getRoot().getPath();
    String nestedFilePath = tmpFolderPath + LONG_PATH_TO_FILE;
    File outputFile = new File(nestedFilePath);

    OutputFilePath tested = new OutputFilePath(mavenLogMock, outputFile);
    tested.createParentDirectories();

    assertFalse("File should not be created on filesystem", outputFile.exists());
    assertTrue("Parent should be created", outputFile.getParentFile().exists());
    assertTrue("Parent should be a folder", outputFile.getParentFile().isDirectory());

    File[] created = {
        new File(tmpFolderPath + "/some/path/to/a"),
        new File(tmpFolderPath + "/some/path/to"),
        new File(tmpFolderPath + "/some/path"),
        new File(tmpFolderPath + "/some"),
    };
    for (File toDelete : created) {
      boolean deleted = toDelete.delete();
      assert deleted : "Test should clean-up (delete) created folders!";
    }
  }

  @Test
  public void deleteFileIfExists_shouldReportIfFileIsNotSimpleFile() throws IOException {
    File fileMock = mock(File.class);
    when(fileMock.exists()).thenReturn(true);
    when(fileMock.isFile()).thenReturn(false);
    when(fileMock.getCanonicalPath()).thenReturn("/path.xml");

    OutputFilePath tested = new OutputFilePath(mavenLogMock, fileMock);
    tested.deleteFileIfExists();

    String expectedMessage = "Cannot delete '/path.xml' as it exists but is not a file.";
    verify(mavenLogMock, times(ONCE)).error(expectedMessage);
  }

  @Test
  public void deleteFileIfExists_shouldLogIfPreviousFileWasDeleted() throws IOException {
    File fileOnFilesystem = folder.newFile();

    OutputFilePath tested = new OutputFilePath(mavenLogMock, fileOnFilesystem);
    tested.deleteFileIfExists();

    String filePath = fileOnFilesystem.getPath();
    String expectedMessage = "File '" + filePath + "' was deleted. Success: 'true'.";
    verify(mavenLogMock, times(ONCE)).debug(expectedMessage);
  }

  @Test
  public void constructor_shouldReadAbsolutePathIfCanonicalPathThrowsException()
      throws IOException {
    File fileMock = mock(File.class);
    when(fileMock.getCanonicalPath()).thenThrow(new IOException("Fake exception."));
    when(fileMock.getAbsolutePath()).thenReturn("/path.xml");

    OutputFilePath tested = new OutputFilePath(mavenLogMock, fileMock);

    assertThat(tested.get(), equalTo("/path.xml"));
  }

  @Test
  public void constructor_shouldReportSecurityException()
      throws IOException {
    File fileMock = mock(File.class);
    when(fileMock.getCanonicalPath()).thenThrow(new IOException("Fake exception."));
    when(fileMock.getAbsolutePath()).thenThrow(new SecurityException("Fake exception."));

    OutputFilePath tested = new OutputFilePath(mavenLogMock, fileMock);

    String expectedMessagePrefix = "Error while getting absolute path";
    verify(mavenLogMock, times(ONCE)).error(startsWith(expectedMessagePrefix));
  }
}
