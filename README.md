Sling Filters Maven Plugin
==========================

[![Build Status](https://travis-ci.org/wiiitek/sling-filters-maven-plugin.svg?branch=master)](https://travis-ci.org/wiiitek/sling-filters-maven-plugin)
[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/cognifide/aet.svg?label=License)](http://www.apache.org/licenses/)

This plugin reads [Jade] files and creates [filter.xml] file for [content package].

It removes code duplication if you are using same filters in different Maven modules.

You may have separate Maven modules for your sample content, assets and application. During development process it is convenient to build ZIP package that contains only application code. The assets and sample content might be built separately - when there is a need to change it.

However when making a release you usually want to have one single package with all parts. The filters for this package need to be in sync with the filters for submodules. This is where you can benefit from Sling Filters Maven Plugin.

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
    <groupId>pl.kubiczak.maven</groupId>
    <artifactId>sling-filters-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
      <executions>
        <execution>
          <goals>
            <goal>generate</goal>
          </goals>
          <configuration>
            <inputFiles>
              <file>${project.basedir}/src/main/resources/filter.jade</file>
            </inputFiles>
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

[Jade]: http://jade-lang.com/
[filter.xml]: http://jackrabbit.apache.org/filevault/filter.html
[content package]: https://helpx.adobe.com/experience-manager/6-3/sites/administering/using/package-manager.html
