File filters = new File(basedir, "/filters/filter.xml")

assert filters.exists()
assert filters.isFile()

String xml = filters.text

assert xml.equals('''<?xml version="1.0" encoding="UTF-8"?>
<workspaceFilter version="1.0">
  <filter root="/apps/sample"/>
  <filter root="/content/sample"/>
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
