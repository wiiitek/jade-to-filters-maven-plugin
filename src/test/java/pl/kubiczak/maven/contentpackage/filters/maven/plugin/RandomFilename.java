package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.util.Random;

class RandomFilename {

  String getNext(String extension) {
    int number = new Random(System.currentTimeMillis()).nextInt(999_999_999);
    return String.format("file-%d.%s", number, extension);
  }
}
