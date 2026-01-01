String expectedFiltersLocation = "/filters/filter.xml"
File actual = new File(basedir, expectedFiltersLocation)

assert actual.exists()
assert actual.isFile()

String xml = actual.text

assert xml.equals('''<?xml version="1.0" encoding="UTF-8"?>
<workspaceFilter version="1.0">
  <filter root="/content/test">
    <exclude pattern="/content/test(/.*)?/qa(/.*)?"/>
  </filter>
  <filter root="/content/dam/test">
    <exclude pattern="/content/dam/test(/.*)?/renditions(/.*)?"/>
    <include pattern="/content/dam/test(/.*)?/renditions/original"/>
    <!-- exclude QA content from DAM -->
    <exclude pattern="/content/dam/test(/.*)?/qa(/.*)?"/>
  </filter>
</workspaceFilter>
''')
