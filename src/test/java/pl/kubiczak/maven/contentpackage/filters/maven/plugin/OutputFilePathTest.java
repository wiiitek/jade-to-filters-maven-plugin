package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OutputFilePathTest {

  private static final String LONG_PATH_TO_FILE = "/some/path/to/a/file.xml";

  private static final int ONCE = 1;

  @Mock
  private Log mavenLogMock;

  @Test
  public void createParentDirectories_shouldNotCreateAFile(@TempDir Path tempDir)
      throws MojoExecutionException {
    String tmpFolderPath = tempDir.toAbsolutePath().toString();
    String nestedFilePath = tmpFolderPath + LONG_PATH_TO_FILE;
    File outputFile = new File(nestedFilePath);

    OutputFilePath tested = new OutputFilePath(mavenLogMock, outputFile);
    tested.createParentDirectories();

    assertFalse(outputFile.exists(), "File should not be created on filesystem");
    assertTrue(outputFile.getParentFile().exists(), "Parent should be created");
    assertTrue(outputFile.getParentFile().isDirectory(), "Parent should be a folder");

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
  public void deleteFileIfExists_shouldLogIfPreviousFileWasDeleted(@TempDir Path tempDir)
      throws IOException {
    File fileOnFilesystem = tempDir.resolve("sample.xml").toFile();
    boolean created = fileOnFilesystem.createNewFile();
    assertThat(created).isTrue();

    OutputFilePath tested = new OutputFilePath(mavenLogMock, fileOnFilesystem);
    tested.deleteFileIfExists();

    String filePath = fileOnFilesystem.getCanonicalPath();
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

    assertThat(tested.get()).isEqualTo("/path.xml");
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
