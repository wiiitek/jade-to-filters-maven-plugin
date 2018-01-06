package pl.kubiczak.maven.contentpackage.filters.maven.plugin;

import java.util.Random;

class RandomFilename {

  private final Random random;

  RandomFilename() {
    random = new Random(System.currentTimeMillis());
  }

  String getNext(String extension) {
    Integer randomInt = new Random(System.currentTimeMillis()).nextInt();
    long number = Math.abs(randomInt.longValue());
    return String.format("file-%d.%s", number, extension);
  }
}
