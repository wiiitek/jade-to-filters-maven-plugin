Jade to Filters Maven Plugin
============================

[![Build Status](https://github.com/wiiitek/jade-to-filters-maven-plugin/actions/workflows/main.yml/badge.svg)](https://github.com/wiiitek/jade-to-filters-maven-plugin/actions/workflows/main.yml)
[![sonarcloud.io](https://sonarcloud.io/api/project_badges/measure?project=pl.kubiczak%3Ajade-to-filters-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=pl.kubiczak%3Ajade-to-filters-maven-plugin)
[![Apache License, Version 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pl.kubiczak/jade-to-filters-maven-plugin/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22pl.kubiczak%22%20AND%20a%3A%22jade-to-filters-maven-plugin%22)

This plugin reads [Jade] files and creates [filter.xml] file for [content package].

To read more about the Jackrabbit Vault filters see:

* [Adobe AEM documentation about package filters](https://helpx.adobe.com/pl/experience-manager/6-3/sites/administering/using/package-manager.html#PackageFilters)
* [Stackoverflow explanation](https://stackoverflow.com/a/25267312) of how filters work
* Documentation for [plugin that generates Vault packages](http://jackrabbit.apache.org/filevault-package-maven-plugin/index.html)

It removes code duplication if you are using same filters in different Maven modules.

You may have separate Maven modules for your sample content, assets and application. During development process it is convenient to only build ZIP package that contains application code. The assets and sample content packages need to be built only when they change.

However when making a release you usually want to have one single package with all parts. The filters for this package need to be in sync with the filters for submodules. This is where you can benefit from Jade to Filters Maven Plugin.

Usage
-----

Add Jade file `src/main/resources/filter.jade` with filters definition

```yaml
filter(root="/content/project")
  exclude(pattern="/content/project(/.*)?/qa(/.*)?")
filter(root="/content/project/dam")
  exclude(pattern="/content/project/dam(/.*)?/renditions(/.*)?")
  include(pattern="/content/project/dam(/.*)?/renditions/original")
  exclude(pattern="/content/project/dam(/.*)?/qa(/.*)?")
```

Add plugin in your pom.xml:

```xml
<build>
  <plugin>
    <groupId>pl.kubiczak</groupId>
    <artifactId>jade-to-filters-maven-plugin</artifactId>
    <version>1.0.0-RC2</version>
      <executions>
        <execution>
          <goals>
            <goal>generate</goal>
          </goals>
          <configuration>
            <inputFiles>
              <file>${project.basedir}/src/main/resources/filter.jade</file>
            </inputFiles>
            <outputFile>${project.basedir}/src/main/resources/META-INF/vault/filter.xml</outputFile>
          </configuration>
        </execution>
      </executions>
  </plugin>
</build>
```

You could use more Jade files. They will be all converted into filters:

```xml
<configuration>
  <inputFiles>
    <file>${project.basedir}/src/main/resources/filter-content.jade</file>
    <file>${project.basedir}/src/main/resources/filter-dam.jade</file>
  </inputFiles>
</configuration>
```

### Comments in Jade files

You could add [comments in Jade files] i.e.:

```yaml
filter(root="/content/project")
  // let's exclude qa content from package
  exclude(pattern="/content/project(/.*)?/qa(/.*)?")
```

Release process
---------------

[Maven release plugin] is used for making a release for this project.

Please execute:

```
mvn release:prepare -B -DreleaseVersion=1.0.0 -DdevelopmentVersion=1.0.1-SNAPSHOT
mvn release:perform -Psources,javadoc,sign
```

[Jade]: http://jade-lang.com/
[filter.xml]: http://jackrabbit.apache.org/filevault/filter.html
[content package]: https://helpx.adobe.com/experience-manager/6-3/sites/administering/using/package-manager.html
[comments in Jade files]: http://jade-lang.com/reference/comments
[Maven release plugin]: http://maven.apache.org/maven-release/maven-release-plugin/index.html
