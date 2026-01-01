package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OutputFileWriterHelperTest {

  private static final String LONG_PATH_TO_FILE = "some/path/to/a/file.xml";

  private static final int ONCE = 1;

  @Mock
  private Log mavenLogMock;

  @Test
  public void createParentDirectories_shouldNotCreateAFile(@TempDir Path tempDir)
      throws MojoExecutionException, IOException {
    Path outputFilePath = tempDir.resolve(LONG_PATH_TO_FILE);
    Path parentFolder = outputFilePath.getParent();

    OutputFileWriterHelper tested = new OutputFileWriterHelper(mavenLogMock, outputFilePath);
    tested.createParentDirectories();

    assertFalse(Files.exists(outputFilePath), "File should not yet be created");
    assertTrue(Files.exists(parentFolder), "Parent should be created");
    assertTrue(Files.isDirectory(parentFolder), "Parent should be a folder");

    // Clean up - delete created directories
    Path[] created = {
        tempDir.resolve("some/path/to/a"),
        tempDir.resolve("some/path/to"),
        tempDir.resolve("some/path"),
        tempDir.resolve("some"),
    };
    for (Path toDelete : created) {
      boolean deleted = Files.deleteIfExists(toDelete);
      assert deleted : "Test should clean-up (delete) created folders!";
    }
  }

  @Test
  public void deleteFileIfExists_shouldReportIfOutputIsAFolder(@TempDir Path folder)
      throws MojoExecutionException {
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

    OutputFileWriterHelper tested = new OutputFileWriterHelper(mavenLogMock, folder);
    tested.deleteFileIfExists();

    verify(mavenLogMock, times(ONCE)).error(argumentCaptor.capture());
    String actualMessage = argumentCaptor.getValue();
    assertThat(actualMessage).startsWith("Cannot delete");
    assertThat(actualMessage).endsWith("as it exists but is not a file.");
  }

  @Test
  public void deleteFileIfExists_shouldLogIfPreviousFileWasDeleted(@TempDir Path tempDir)
      throws MojoExecutionException, IOException {
    Path fileOnFilesystem = tempDir.resolve("sample.xml");
    Files.createFile(fileOnFilesystem);
    assertThat(Files.isRegularFile(fileOnFilesystem)).isTrue();

    OutputFileWriterHelper tested = new OutputFileWriterHelper(mavenLogMock, fileOnFilesystem);
    tested.deleteFileIfExists();

    String filePath = fileOnFilesystem.toAbsolutePath().toString();
    String expectedMessage = "File '" + filePath + "' was deleted.";
    verify(mavenLogMock, times(ONCE)).debug(expectedMessage);
  }
}